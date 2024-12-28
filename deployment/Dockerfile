FROM openjdk:19-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy Gradle wrapper and necessary files
COPY ../gradlew .
COPY ../gradle ./gradle
COPY ../build.gradle.kts .
COPY ../settings.gradle.kts .
COPY ../build/libs/*.jar /app/app.jar

# Expose the port the Spring Boot app will run on
EXPOSE 8080

# Set the command to run the Spring Boot application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]