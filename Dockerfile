# Etapa 1: Compilación con Maven global y Java 21 (o cambia a la versión que use tu proyecto)
FROM eclipse-temurin:25-alpine AS build
WORKDIR /app

# Instalar maven para compilar directamente
RUN apt-get update && apt-get install -y maven

# Copiar el pom.xml y el código fuente
COPY pom.xml ./
COPY src ./src

# Compilar usando mvn directamente (sin depender del wrapper)
RUN mvn clean package -DskipTests

# Etapa 2: Imagen ligera para ejecución
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copiar el jar generado desde la etapa de compilación
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]