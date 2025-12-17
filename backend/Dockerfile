# Usamos una imagen de Java 17 de Eclipse Temurin
FROM eclipse-temurin:17-jdk-jammy

# Establecemos el directorio de trabajo
WORKDIR /app

# Copiamos todo el código fuente
COPY . /app

# Instalamos dependencias y construimos el JAR
RUN ./gradlew bootJar -x test

# Exponemos el puerto
EXPOSE 8080

# Comando para ejecutar el JAR en el puerto asignado
CMD ["java", "-Dserver.port=$PORT", "-jar", "build/libs/*-boot.jar"]