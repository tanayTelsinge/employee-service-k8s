# Stage 1 — Build
FROM eclipse-temurin:25-jdk AS builder
WORKDIR /app
COPY . .
RUN ./gradlew bootJar --no-daemon

# Stage 2 — Run
FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Duser.timezone=UTC", "-jar", "app.jar"]