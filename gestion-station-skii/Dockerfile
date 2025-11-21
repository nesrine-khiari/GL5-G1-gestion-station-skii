# Use a lightweight Java 17 image
FROM openjdk:17.0.2-jdk-slim

# Set working directory
WORKDIR /app

# Copy the built Spring Boot jar (update the name if different)
COPY target/gestion-station-skii-0.0.1-SNAPSHOT.jar app.jar

# Expose the port your app runs on
EXPOSE 8087

# Run the Spring Boot app
ENTRYPOINT ["java", "-jar", "app.jar"]
