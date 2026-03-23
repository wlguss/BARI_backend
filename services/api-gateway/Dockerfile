FROM gradle:8.7-jdk17 AS builder

WORKDIR /app

COPY build.gradle settings.gradle ./
COPY gradle ./gradle
COPY gradlew ./gradlew

COPY libs ./libs
COPY services ./services

RUN chmod +x ./gradlew
RUN sed -i 's/\r$//' ./gradlew

RUN ./gradlew :services:api-gateway:bootJar --no-daemon --stacktrace --warning-mode all

FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY --from=builder /app/services/api-gateway/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]