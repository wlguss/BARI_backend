FROM --platform=linux/amd64 eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./
COPY libs libs
COPY services/user-service/build.gradle services/user-service/build.gradle

RUN chmod +x gradlew
RUN ./gradlew :services:user-service:dependencies

COPY services/user-service/src services/user-service/src
RUN ./gradlew :services:user-service:bootJar -x test

FROM --platform=linux/amd64 eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=builder /app/services/user-service/build/libs/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
