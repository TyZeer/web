FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY . .
RUN chmod +x mvnw
RUN ./mvnw clean package
COPY target/mp3-manager-1.0.0-SNAPSHOT.jar /app
EXPOSE 8083
CMD ["java", "-jar", "mp3-manager-1.0.0-SNAPSHOT.jar"]
