# Paso 1: Usar una imagen base de Java 17 o 21 (según la versión de tu proyecto)
FROM eclipse-temurin:21-jre-alpine

# Directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiar el ejecutable jar generado por Maven al contenedor
COPY target/*.jar app.jar

# Exponer el puerto predeterminado de Spring Boot
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]