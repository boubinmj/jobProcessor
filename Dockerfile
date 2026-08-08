# Use Ubuntu as base image
FROM ubuntu:latest

# Set working directory
WORKDIR /app

# Install Java 17 JDK and Maven
RUN apt-get update && apt-get install -y \
    openjdk-17-jdk-headless \
    maven \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# Copy project files
COPY pom.xml .
COPY src ./src

# Build the project with Maven
RUN mvn clean package -DskipTests

# Expose port 8080
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "target/myProject-0.0.1-SNAPSHOT.jar"]
