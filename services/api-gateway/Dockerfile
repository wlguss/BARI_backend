FROM --platform=linux/amd64 eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./
COPY libs libs
COPY services/api-gateway/build.gradle services/api-gateway/build.gradle

RUN chmod +x gradlew
RUN ./gradlew :services:api-gateway:dependencies

COPY services/api-gateway/src services/api-gateway/src
RUN ./gradlew :services:api-gateway:bootJar -x test

FROM --platform=linux/amd64 eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=builder /app/services/api-gateway/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
