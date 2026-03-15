# ---- Etapa de Compilación (Build Stage) ----
# Usamos una imagen oficial de Maven con OpenJDK 17.
# Asegúrate de que la versión de Java coincida con la de tu proyecto.
FROM maven:3.8.5-openjdk-17 AS build

# Establecemos el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiamos primero el pom.xml para aprovechar el cache de capas de Docker.
# Docker solo volverá a descargar las dependencias si el pom.xml cambia.
COPY pom.xml .

# Descargamos todas las dependencias del proyecto
RUN mvn dependency:go-offline

# Copiamos el resto del código fuente de la aplicación
COPY src ./src

# Compilamos la aplicación y la empaquetamos en un JAR.
# -DskipTests omite la ejecución de tests durante la creación de la imagen.
RUN mvn package -DskipTests


# ---- Etapa de Ejecución (Run Stage) ----
# Usamos una imagen ligera de OpenJDK 17 solo con el JRE.
FROM eclipse-temurin:17-jre-focal

# Establecemos el directorio de trabajo
WORKDIR /app

# Exponemos el puerto 8080, que es el puerto por defecto de Spring Boot.
# Render usará esto para redirigir el tráfico a tu aplicación.
EXPOSE 8080

# Copiamos el archivo JAR generado en la etapa de compilación a la imagen final.
# La ruta del JAR puede variar según la configuración de tu pom.xml (artifactId-version.jar).
# Asumo que el nombre del JAR es SistemaDeVerificacion-0.0.1-SNAPSHOT.jar.
# ¡Verifica esto en tu carpeta `target` después de compilar!
COPY --from=build /app/target/SistemaDeVerificacion-0.0.1-SNAPSHOT.jar app.jar

# Comando para ejecutar la aplicación cuando se inicie el contenedor.
ENTRYPOINT ["java", "-jar", "app.jar"]
