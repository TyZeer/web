# Use the official OpenJDK image as the base image
FROM openjdk:17-jdk-alpine

# Set the working directory in the container
WORKDIR /app

# Copy the Maven project file
COPY pom.xml .

# Copy the source code
COPY src ./src

RUN chmod +x mvnw

# Package the application
RUN ./mvnw package -DskipTests

# Expose the port the application runs on
EXPOSE 8080

# Run the Spring Boot application
CMD ["java", "-jar", "target/mp3-manager-0.0.1-SNAPSHOT.jar"]
