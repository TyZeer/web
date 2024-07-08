FROM openjdk:17-jdk-alpine AS builder
WORKDIR /app

# Copy the Maven wrapper script and the POM file
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Build all the dependencies in preparation to go offline.
# This is a separate step so the dependencies will be cached unless
# the pom changes.
COPY src src
RUN ./mvnw dependency:go-offline -B

# Package the application
COPY src src
RUN ./mvnw package -DskipTests

# Second stage: build the final image
FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY --from=builder /app/target/mp3-manager-1.0.0-SNAPSHOT.jar /app
EXPOSE 8083
EXPOSE 5432
CMD ["java", "-jar", "mp3-manager-1.0.0-SNAPSHOT.jar"]
