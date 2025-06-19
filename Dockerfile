# --- Build Stage ---
FROM gradle:8.5-jdk17 AS builder

WORKDIR /app

# Copy project files
COPY --chown=gradle:gradle . .

# Build the application (skip tests if desired)
RUN gradle clean bootJar -x test

# --- Run Stage ---
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy built JAR from builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]