
# Receiver IoT

Este proyecto es una aplicación desarrollada en **Spring Boot** para recibir, procesar y almacenar datos de dispositivos IoT que se comunican a través del protocolo **MQTT**. Los mensajes de los dispositivos son procesados y almacenados en una base de datos **TimescaleDB**, con el uso de TLS y autenticación mediante usuario y contraseña para garantizar la seguridad de las comunicaciones.

## Características

- **Recepción de datos vía MQTT**: La aplicación escucha mensajes provenientes de dispositivos IoT a través de un broker MQTT.
- **Seguridad**: Uso de **TLS** y autenticación con usuario y contraseña en el broker MQTT.
- **Almacenamiento en TimescaleDB**: Los datos recibidos son almacenados en una base de datos TimescaleDB, optimizada para series temporales.
- **Manejo de diferentes dispositivos**: Soporte para múltiples dispositivos con identificadores únicos (clientId).
- **Organización de datos**: Los datos se almacenan en tablas que aprovechan las funcionalidades de TimescaleDB, como las **hypertables** para optimización de series temporales.

## Requisitos

- **Java 11** o superior.
- **Docker y Docker Compose** para ejecutar TimescaleDB.
- **Broker MQTT** con soporte de TLS y autenticación de usuario.
- **Certificado raíz de Let's Encrypt** o similar para conexiones seguras.

## Instalación

### 1. Clonar el repositorio

Clona el repositorio en tu máquina local:

```bash
git clone https://github.com/alvaro-salazar/receiver-iot.git
cd receiver-iot
```

### 2. Configurar las propiedades de la aplicación

Modifica el archivo **`src/main/resources/application.properties`** para configurar las conexiones al broker MQTT y a la base de datos TimescaleDB.

Ejemplo de configuración:

```properties
# Configuración del broker MQTT
mqtt.broker.url=tcp://your-broker-url:8883
mqtt.username=device1
mqtt.password=a1b2c3d4
mqtt.clientId=ESP32-XXXXXX

# Configuración de la base de datos TimescaleDB
spring.datasource.url=jdbc:postgresql://localhost:5432/iotdb
spring.datasource.username=postgres
spring.datasource.password=password
```

### 3. Ejecutar TimescaleDB con Docker

Este proyecto incluye un archivo `docker-compose.yml` que configurará y levantará **TimescaleDB**. Puedes ejecutar el siguiente comando para iniciarlo:

```bash
docker-compose up -d
```

### 4. Compilar y ejecutar la aplicación

Compila y ejecuta la aplicación usando **Maven**:

```bash
mvn clean install
mvn spring-boot:run
```

### 5. Visualización de datos

Una vez que la aplicación esté ejecutándose, los datos enviados por los dispositivos IoT serán almacenados en **TimescaleDB**. Puedes acceder a la base de datos utilizando un cliente PostgreSQL para visualizar los datos.

### Ejemplo de consulta SQL en TimescaleDB:

```sql
SELECT * FROM data_entity WHERE time > NOW() - INTERVAL '1 hour';
```

## Esquema de Tópicos MQTT

Los tópicos en los que la aplicación publica y suscribe datos tienen la siguiente estructura:

```plaintext
<pais>/<estado>/<ciudad>/<device-id>/<usuario>/out  // Para publicaciones
<pais>/<estado>/<ciudad>/<device-id>/<usuario>/in   // Para suscripciones
```

### Ejemplo:

```plaintext
colombia/valle/tulua/ESP32-CC50E3B65DD/device1/out
```

## Funcionalidades Principales

- **Recepción de datos**: La aplicación escucha los datos enviados por los dispositivos IoT a través de MQTT.
- **Procesamiento de datos**: Los datos se procesan para obtener variables como temperatura, humedad, entre otras, y se almacenan en la base de datos.
- **Reconexión automática**: La aplicación maneja desconexiones de forma automática para reconectarse al broker MQTT en caso de fallos en la conexión.

## Seguridad

- **Conexiones seguras**: El uso de **TLS** asegura que las comunicaciones entre la aplicación y el broker MQTT estén cifradas.
- **Autenticación**: La autenticación mediante usuario y contraseña garantiza que solo dispositivos autorizados puedan conectarse.

## Contribuciones

Las contribuciones son bienvenidas. Si deseas colaborar en el proyecto, abre un **issue** o envía un **pull request** con tus sugerencias.

## Licencia

Este proyecto está bajo la licencia MIT. Consulta el archivo [LICENSE](LICENSE) para más información.
