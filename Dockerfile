# Etapa 1: Compilación con Maven
FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app

# Copiar archivos de configuración de Maven y código fuente
COPY pom.xml ./
COPY mvnw ./
COPY src ./src

# Si tu proyecto usa la carpeta .mvn, descomenta la siguiente línea:
# COPY .mvn .mvn

# Dar permisos de ejecución al wrapper y empaquetar la app
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

# Etapa 2: Imagen ligera para ejecución
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copiar el jar generado
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]