FROM --platform=linux/amd64 eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./
COPY libs libs
COPY services/item-service/build.gradle services/item-service/build.gradle

RUN chmod +x gradlew
RUN ./gradlew :services:item-service:dependencies

COPY services/item-service/src services/item-service/src
RUN ./gradlew :services:item-service:bootJar -x test

FROM --platform=linux/amd64 eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=builder /app/services/item-service/build/libs/*.jar app.jar

EXPOSE 8087

ENTRYPOINT ["java", "-jar", "app.jar"]
