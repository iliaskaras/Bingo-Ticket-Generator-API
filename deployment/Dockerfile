FROM openjdk:21-jdk-slim AS build

# Stage 1: Copy files and produce the jar file.
WORKDIR /app

COPY ../gradlew .
COPY ../gradle ./gradle
COPY ../build.gradle.kts .
COPY ../settings.gradle.kts .

RUN ./gradlew --no-daemon dependencies

COPY ../src ./src

# Build the project (without tests)
RUN ./gradlew build -x test

# Stage 2: Expose the application's entrypoint.
FROM openjdk:21-jdk-slim

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]