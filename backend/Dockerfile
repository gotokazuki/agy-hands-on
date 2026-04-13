FROM ghcr.io/graalvm/jdk-community:25 AS builder

WORKDIR /app

# Copy the entire project
COPY . .

# Set working directory to backend
WORKDIR /app/backend

# Build the application
# We skip generateJooq and flywayMigrate because we don't have a live database during build.
# We also skip tests for faster feedback.
RUN ./gradlew bootJar -x generateJooq -x flywayMigrate -x test

# Runtime stage
FROM ghcr.io/graalvm/jdk-community:25
WORKDIR /app

# Copy the built fat jar from the builder stage
COPY --from=builder /app/backend/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-jar", "app.jar"]
