FROM eclipse-temurin:8-jdk

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 8087

ENTRYPOINT ["java", "-jar", "app.jar"]
