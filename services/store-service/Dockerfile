FROM --platform=linux/amd64 eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./
COPY libs libs
COPY services/store-service/build.gradle services/store-service/build.gradle

RUN chmod +x gradlew
RUN ./gradlew :services:store-service:dependencies

COPY services/store-service/src services/store-service/src
RUN ./gradlew :services:store-service:bootJar -x test

FROM --platform=linux/amd64 eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=builder /app/services/store-service/build/libs/*.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]
