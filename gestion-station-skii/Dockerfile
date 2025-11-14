# Use OpenJDK 8 base image
FROM openjdk:8-jdk-alpine

# Set working directory
WORKDIR /app

# Copy the JAR file from target directory
COPY target/gestion-station-skii-0.0.1-SNAPSHOT.jar app.jar

# Expose the application port
EXPOSE 8087

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
