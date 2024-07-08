# Use the official OpenJDK image as the base image
FROM openjdk:17-jdk-alpine

# Set the working directory in the container
WORKDIR /app

# Copy the Maven Wrapper files and set the execution permissions
COPY .mvn/ .mvn/
COPY mvnw .
COPY mvnw.cmd .
RUN chmod +x mvnw

# Copy the Maven project file and source code
COPY pom.xml .
COPY src ./src

# Package the application
RUN ./mvnw package -DskipTests

# Expose the port the application runs on
EXPOSE 8080

# Run the Spring Boot application
CMD ["java", "-jar", "target/mp3-manager-0.0.1-SNAPSHOT.jar"]
