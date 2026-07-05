FROM --platform=linux/amd64 eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./
COPY libs libs
COPY services/inventory-service/build.gradle services/inventory-service/build.gradle

RUN chmod +x gradlew
RUN ./gradlew :services:inventory-service:dependencies

COPY services/inventory-service/src services/inventory-service/src
RUN ./gradlew :services:inventory-service:bootJar -x test

FROM --platform=linux/amd64 eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=builder /app/services/inventory-service/build/libs/*.jar app.jar

EXPOSE 8084

ENTRYPOINT ["java", "-jar", "app.jar"]
