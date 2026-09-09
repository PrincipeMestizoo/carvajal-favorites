# Etapa 1: Compilación con Maven
FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app

# Copiar los archivos de configuración de Maven y el código fuente
COPY pom.xml ./
COPY .mvn .mvn
COPY mvnw ./
COPY src src

# Dar permisos de ejecución al wrapper de Maven y empaquetar la app
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

# Etapa 2: Imagen ligera para ejecución
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copiar el jar generado desde la etapa de compilación
COPY --from=build /app/target/*.jar app.jar

# Render asigna dinámicamente un puerto mediante la variable PORT
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]