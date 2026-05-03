# Stage 1: Build the Java Spring Boot application
FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app

# Install Node.js for Vaadin frontend compilation
RUN apt-get update && apt-get install -y curl && \
    curl -fsSL https://deb.nodesource.com/setup_20.x | bash - && \
    apt-get install -y nodejs

# Copy the project files
COPY . .

# Ensure maven wrapper is executable and build the application in production mode
RUN chmod +x ./mvnw
RUN ./mvnw clean package -Pproduction -DskipTests

# Stage 2: Create the production container
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copy the built jar from the build stage
COPY --from=build /app/target/*.jar app.jar

# Cloud Run expects the app to listen on port 8080
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
