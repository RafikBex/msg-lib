# Docker multi-stage para construir y ejecutar la librería de notificaciones.
#
# Requisitos:
# - Docker instalado localmente.
# - No es necesario tener Java 25 ni Maven configurados en la máquina host.
#
# Comandos útiles:
#   docker build -t msglib-connectors .
#   docker run --rm msglib-connectors

# ---------------------------------------------------------------------------
# Etapa 1: Builder
# ---------------------------------------------------------------------------
FROM eclipse-temurin:25-jdk-alpine AS builder

WORKDIR /app

# Instalar Maven en Alpine.
RUN apk add --no-cache maven

# Copiar primero el POM para aprovechar la caché de dependencias de Docker.
COPY pom.xml .

# Descargar dependencias (opcional, mejora el aprovechamiento de caché).
RUN mvn dependency:go-offline -B || true

# Copiar el código fuente y los recursos.
COPY src ./src

# Compilar, empaquetar y copiar las dependencias a target/dependency.
RUN mvn clean package -DskipTests -B && \
    mvn dependency:copy-dependencies -DoutputDirectory=target/dependency -B

# ---------------------------------------------------------------------------
# Etapa 2: Runtime
# ---------------------------------------------------------------------------
FROM eclipse-temurin:25-jdk-alpine

WORKDIR /app

# Copiar el JAR de la librería y todas sus dependencias desde la etapa builder.
COPY --from=builder /app/target/MsgLibConnectors-1.0-SNAPSHOT.jar ./lib/
COPY --from=builder /app/target/dependency ./lib/

# Ejecutar la demo principal (org.challenge.Main).
# Puede cambiarse por otra clase de ejemplo si se añaden más demos.
CMD ["java", "-cp", "lib/*", "org.challenge.Main"]
