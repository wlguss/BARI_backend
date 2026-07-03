FROM --platform=linux/amd64 eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./
COPY libs libs
COPY services/discount-service/build.gradle services/discount-service/build.gradle

RUN chmod +x gradlew
RUN ./gradlew :services:discount-service:dependencies

COPY services/discount-service/src services/discount-service/src
RUN ./gradlew :services:discount-service:bootJar -x test

FROM --platform=linux/amd64 eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=builder /app/services/discount-service/build/libs/*.jar app.jar

EXPOSE 8085

ENTRYPOINT ["java", "-jar", "app.jar"]
