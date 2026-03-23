FROM --platform=linux/amd64 eclipse-temurin:17-jdk-alpine as builder
WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./
COPY libs libs
COPY services/order-service services/order-service

RUN chmod +x gradlew
RUN ./gradlew :services:order-service:dependencies

COPY services/order-service/src services/order-service/src
RUN ./gradlew :services:order-service:bootJar -x test

# ! runtime stage (jre) -> jar
FROM --platform=linux/amd64 eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=builder /app/services/order-service/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
