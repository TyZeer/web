FROM openjdk:17-jdk-alpine AS builder
WORKDIR /app
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src
RUN ./mvnw dependency:go-offline -B
COPY src src
RUN ./mvnw package -DskipTests

# Second stage: build the final image
FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY --from=builder /app/target/mp3-manager-1.0.0-SNAPSHOT.jar /app
EXPOSE 8083
CMD ["java", "-jar", "mp3-manager-1.0.0-SNAPSHOT.jar"]
