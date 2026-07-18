# AGENTS.md - Notas para agentes de código

## Estructura del proyecto

Este es un proyecto Maven de Java 25 ubicado en `D:\Trabajo\Challenge\MsgLibConnectors`.

- **Paquete raíz**: `org.challenge`.
- **API pública**: `org.challenge.notification.api` (no modificar interfaces sin revisar impacto).
- **Punto de entrada**: `org.challenge.Main`.
- **Tests**: el POM incluye JUnit 5 (`junit-jupiter`) y Mockito (`mockito-core`, `mockito-junit-jupiter`) en scope `test`. Hay tests de construcción de instancias y del nuevo proveedor `MockMailgunProvider`.

## Convenciones de código

- Usa **records** para modelos inmutables con poca lógica.
- Los **value objects** (`EmailAddress`, `PhoneNumber`, `DeviceToken`) validan su estado en el constructor compacto.
- Las configuraciones usan **Builders** inmutables y `SecretValue` para credenciales.
- Los adaptadores de proveedor implementan `NotificationProvider<N>`.
- Los decoradores extienden `NotificationProviderDecorator<N>`.
- Las excepciones heredan de `NotificationException` y llevan un `NotificationErrorCode`.
- Los comentarios y Javadoc pueden estar en español, alineados con el estilo existente.

## Cómo compilar y ejecutar

Compilar:

```bash
mvn compile
```

Ejecutar tests:

```bash
mvn test
```

Ejecutar el ejemplo:

```bash
mvn compile exec:java -Dexec.mainClass=org.challenge.Main
```

Ejecutar con Docker (sin Java/Maven locales):

```bash
docker build -t msglib-connectors .
docker run --rm msglib-connectors
```

Con el JDK y Maven específicos del entorno:

```powershell
$env:JAVA_HOME = 'C:\Program Files\Eclipse Adoptium\jdk-25.0.3.9-hotspot'
& 'C:\Users\Rafael\.m2\wrapper\dists\apache-maven-3.9.8-bin\337e6d14\apache-maven-3.9.8\bin\mvn.cmd' compile
```

## Dependencias principales

Ver `pom.xml` para versiones exactas:

- `commons-validator`: validación de emails.
- `commons-lang3`: utilidades de texto.
- `log4j-api` / `log4j-core`: logging.
- `junit-jupiter`: tests con JUnit 5 (scope test).
- `mockito-core` / `mockito-junit-jupiter`: mocking en tests (scope test).

## Consideraciones para futuros cambios

- Si se añade un proveedor real, conservar la interfaz `NotificationProvider` y usar `NotificationDeliveryException` para errores.
- Mantener la separación entre `model`, `validation`, `provider`, `core` y `event`.
- Las credenciales deben seguir usando `SecretValue` para evitar fugas en logs.

## Estado de compilación

La última compilación con Maven y JDK 25 finalizó con `BUILD SUCCESS`.
