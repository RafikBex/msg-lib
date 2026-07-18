# Msg-Lib Connectors

Librería de Java que centraliza el envío de notificaciones a través de múltiples canales (email, SMS y push). El proyecto utiliza un pipeline configurable basado en validación, selección de proveedor, envío y publicación de eventos.

## Requisitos

- **Java 25** (se fuerza el target/source del compilador a 25).
- **Maven 3.9.8** (incluido en el wrapper del proyecto) o cualquier Maven compatible.

## Cómo compilar

Desde el directorio raíz del proyecto ejecuta:

```bash
mvn compile
```

## Cómo ejecutar

El punto de entrada principal es `org.challenge.Main`:

```bash
mvn compile exec:java -Dexec.mainClass=org.challenge.Main
```

Esto ejecuta los ejemplos síncronos y asíncronos usando proveedores simulados.

## Cómo ejecutar tests

```bash
mvn test
```

Los tests usan JUnit 5 y Mockito.

## Docker

El proyecto incluye un `Dockerfile` multi-stage que permite compilar y ejecutar la demo sin tener Java 25 ni Maven instalados localmente.

### Construir la imagen

```bash
docker build -t msglib-connectors .
```

### Ejecutar la demo

```bash
docker run --rm msglib-connectors
```

El comando anterior ejecuta `org.challenge.Main`, que corre los ejemplos síncronos y asíncronos usando los proveedores simulados.

### ¿Qué hace el Dockerfile?

1. **Etapa `builder`**: usa `eclipse-temurin:25-jdk-alpine`, instala Maven, compila la librería con `mvn clean package` y copia las dependencias a `target/dependency`.
2. **Etapa `runtime`**: copia el JAR de la librería y sus dependencias a una imagen liviana con Java 25.
3. **CMD**: ejecuta la clase principal `org.challenge.Main` usando el classpath con todos los JARs.

## Estructura del proyecto

```
src/main/java/org/challenge/
├── Main.java                       # Punto de entrada y configuración de ejemplo
├── config/                         # Configuraciones de proveedores (SendGrid, Twilio, Firebase, Mailgun)
├── core/                           # Cliente, registro de pipelines y pipelines de canal
├── decorator/                      # Decoradores de proveedores (reintentos)
├── decorator/retry/                # Política de reintentos y utilidades de espera
├── event/                          # Eventos y publicadores (in-memory, no-op)
├── exception/                      # Excepciones y códigos de error
├── model/                          # Modelos de dominio y value objects
│   ├── email/
│   ├── push/
│   └── sms/
├── notification/api/               # API pública del cliente (interfaces, records, enums)
├── provider/                       # Contratos de proveedores y estrategias de ruteo
└── provider/adapter/               # Adaptadores simulados de cada proveedor
    ├── email/
    ├── push/
    └── sms/
```

## Módulos principales

- **core**: contiene `DefaultNotificationClient`, `HandlerRegistry`, `ChannelPipeline` y `DefaultChannelPipeline`. Es el motor de procesamiento de notificaciones.
- **provider**: define `NotificationProvider`, `ProviderRoutingStrategy` y la estrategia `FixedProviderRouting`. También alberga los adaptadores simulados (`MockSendGridProvider`, `MockMailgunProvider`, `MockTwilioProvider`, `MockFirebaseProvider`).
- **validation**: implementa `NotificationValidator` para email, SMS y push, verificando campos obligatorios y formatos.
- **config**: clases de configuración inmutable (`SendGridConfig`, `TwilioSmsConfig`, `FirebaseConfig`, `MailgunConfig`) con `SecretValue` para ocultar credenciales.
- **event**: publicación de eventos `NotificationSentEvent` y `NotificationFailedEvent` a través de `InMemoryNotificationEventPublisher` o `NoOpNotificationEventPublisher`.
- **model**: value objects (`EmailAddress`, `PhoneNumber`, `DeviceToken`) y records de notificación (`EmailNotification`, `SmsNotification`, `PushNotification`).
- **exception**: jerarquía de excepciones con `NotificationException` como base y códigos de error en `NotificationErrorCode`.

## Dependencias principales

- `commons-validator`: validación de direcciones de email.
- `commons-lang3`: utilidades de texto.
- `log4j-api` y `log4j-core`: logging.
- `junit-jupiter`: tests con JUnit 5 (scope test).
- `mockito-core` / `mockito-junit-jupiter`: mocking en tests (scope test).

## Notas

- Los proveedores actuales son simulados y no realizan llamadas de red reales.
- El `Main` de ejemplo lee variables de entorno o usa valores simulados por defecto.
- El envío en lote (`sendBatch`) es síncrono y secuencial en la implementación actual.
- El proyecto se compila con `source` y `target` 25 y usa características como records, sealed interfaces y virtual threads.

