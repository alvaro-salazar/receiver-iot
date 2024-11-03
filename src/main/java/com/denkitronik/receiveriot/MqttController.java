package com.denkitronik.receiveriot;


import com.denkitronik.receiveriot.entities.Location;
import com.denkitronik.receiveriot.entities.Measurement;
import com.denkitronik.receiveriot.entities.Device;
import com.denkitronik.receiveriot.entities.User;
import com.denkitronik.receiveriot.services.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

import javax.net.ssl.SSLSocketFactory;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.SSLContext;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.time.ZonedDateTime;
import java.util.Map;

/**
 * Clase que se encarga de recibir y procesar los mensajes MQTT
 */
@Component
public class MqttController {

    private MqttAsyncClient client;                         // Cliente MQTT de Eclipse Paho
    private final UserService userService;                  // Servicio de usuario
    private final LocationService locationService;          // Servicio de ubicación (ciudad, estado, país)
    private final MeasurementService measurementService;    // Servicio de medición (variable)
    private final DeviceService deviceService;              // Servicio de sensor
    private final DataService dataService;                  // Servicio de datos (mediciones)
    private static final Logger logger = LoggerFactory.getLogger(MqttController.class);

    @Value("${mqtt.broker.url}")    // Lee de application.properties el valor de mqtt.broker.url
    private String brokerUrl;
    @Value("${mqtt.client.id}")     // Lee de application.properties el valor de mqtt.client.id
    private String clientId;
    @Value("${mqtt.username}")      // Lee de application.properties el valor de mqtt.username
    private String username;
    @Value("${mqtt.password}")      // Lee de application.properties el valor de mqtt.password
    private String password;
    @Value("${mqtt.qos}")           // Lee de application.properties el valor de mqtt.qos
    private int qos;
    @Value("${mqtt.topic}")         // Lee de application.properties el valor de mqtt.topic
    private String topic;


    /**
     * Constructor de la clase MqttController que inyecta los servicios necesarios
     */
    public MqttController(UserService userService, LocationService locationService, MeasurementService measurementService, DeviceService deviceService, DataService dataService) {
        this.userService = userService;
        this.locationService = locationService;
        this.measurementService = measurementService;
        this.deviceService = deviceService;
        this.dataService = dataService;
    }

    /**
     * Metodo de inicialización que se ejecuta al arrancar la aplicación
     */
    @PostConstruct
    public void init() throws CertificateException, KeyStoreException, MqttException, NoSuchAlgorithmException, IOException, KeyManagementException {

        // Cargar el certificado de Let's Encrypt para TLS
        SSLSocketFactory socketFactory = getSocketFactoryWithLetsEncrypt();

        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(this.username);
        options.setPassword(this.password.toCharArray());
        options.setAutomaticReconnect(true);
        options.setKeepAliveInterval(60);
        options.setConnectionTimeout(30);
        options.setCleanSession(false);
        options.setSocketFactory(socketFactory);  // Configuración de TLS

        // Crear el cliente MQTT con el ID, el broker URL y con persistencia en memoria
        client = new MqttAsyncClient(this.brokerUrl, this.generateClientId(), new MemoryPersistence());
        MqttCallback callback = new MqttCallback() {

            // Metodo que se ejecuta cuando se pierde la conexión
            @Override
            public void connectionLost(Throwable cause) {
                logger.error("Conexión perdida: {}", cause.getMessage());
                logger.info("Intento de reconexion al broker MQTT...");
                connect(options, true); // Intentar reconectar
                logger.info("Reconectado al broker MQTT");
            }

            // Metodo que se ejecuta cuando se recibe un mensaje
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                processMessage(topic, message); // Procesar el mensaje recibido
            }

            // Metodo que se ejecuta cuando se completa la entrega de un mensaje
            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                logger.info("Entrega completa");
            }
        };
        client.setCallback(callback);  // Establecer el callback
        this.connect(options, false); // Conectar al broker MQTT
    }

    /**
     * Metodo que se encarga de conectar el cliente MQTT
     */
    private void connect(MqttConnectOptions options, boolean reconnect) {
        try {
            if (reconnect) {
                logger.info("Reconectando al broker MQTT: {}", this.brokerUrl);
                if (client.isConnected()) {
                    logger.info("El cliente aun esta en estado conectado");
                    IMqttToken disconnectToken = this.client.disconnect();
                    disconnectToken.waitForCompletion(10000);
                    logger.info("Desconexion exitosa");
                }
            }
            logger.info("Conectando al broker MQTT: {}", this.brokerUrl);
            client.connect(options).waitForCompletion();
            logger.info("Conexión exitosa");
            client.subscribe(this.topic, this.qos);
            logger.info("Suscrito al tópico: {}", this.topic);
        } catch (MqttException e) {
            logger.error("Error al conectar: {}", e.getMessage());
        }
    }

    /**
     * Metodo que se encarga de procesar los mensajes recibidos
     *
     * @param topic   Tópico del mensaje
     * @param message Mensaje MQTT
     */
    private void processMessage(String topic, MqttMessage message) {
        String payload = new String(message.getPayload());
        logger.info("Mensaje recibido!!: {}", payload);

        // Extraer datos del tópico
        String[] topicData = topic.split("/");        
        String country = topicData[0];
        String state = topicData[1];
        String city = topicData[2];
        String deviceId = topicData[3];
        String user = topicData[4];

        // Obtener o crear el usuario y la ubicación
        User userObj = userService.getUser(user);
        Location locationObj = locationService.getOrCreateLocation(city, state, country);

        // Crear un ObjectMapper para procesar el payload JSON
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // Convertir el payload JSON a un Map
            Map<String, Object> jsonPayload = objectMapper.readValue(payload, Map.class);

            // Iterar sobre las variables del JSON para registrar las mediciones
            for (Map.Entry<String, Object> entry : jsonPayload.entrySet()) {
                String variable = entry.getKey();
                float value = ((Number) entry.getValue()).floatValue();

                // Obtener o crear la variable de medición
                Measurement variableObj = measurementService.getOrCreateMeasurement(variable);

                // Obtener o crear el dispositivo asociado al usuario y la ubicación
                Device deviceObj = deviceService.getOrCreateDevice(deviceId, userObj, locationObj);

                // Registrar la medición en la base de datos
                dataService.createData(value, deviceObj, variableObj, ZonedDateTime.now());
            }
        } catch (Exception e) {
            // Manejo de excepciones en caso de error al procesar el JSON o almacenar datos
            logger.error("Error al procesar el payload JSON: {}", e.getMessage());
        }
    }

    /**
     * Metodo que se encarga de configurar el contexto SSL con el certificado público de Let's Encrypt
     *
     * @return SocketFactory configurado con el certificado de Let's Encrypt
     * @throws Exception Excepción en caso de error al cargar el certificado
     */
    private SSLSocketFactory getSocketFactoryWithLetsEncrypt() throws CertificateException, KeyStoreException, NoSuchAlgorithmException, IOException, KeyManagementException {
        // Cargar el certificado público de Let's Encrypt desde src/main/resources
        InputStream fis = getClass().getClassLoader().getResourceAsStream("isrgrootx1.pem");

        if (fis == null) {
            throw new FileNotFoundException("No se pudo encontrar el archivo de certificado 'isrgrootx1.pem'");
        }

        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509Certificate caCert = (X509Certificate) cf.generateCertificate(fis);

        // Configurar KeyStore con el certificado público
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(null, null);
        ks.setCertificateEntry("caCert", caCert);

        // Configurar TrustManager con el KeyStore
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(ks);

        // Crear el contexto SSL con el TrustManager
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), null);

        return sslContext.getSocketFactory();
    }

    /**
     * Metodo que se encarga de generar un ID de cliente único MQTT
     *
     * @return ID de cliente único
     */
    public String generateClientId() {
        String hostname = "unknown";
        try {
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            logger.error("Error al obtener el nombre del host: {}", e.getMessage());
        }
        long timestamp = System.currentTimeMillis();
        return this.clientId + "-" + hostname + "-" + timestamp;
    }
}
