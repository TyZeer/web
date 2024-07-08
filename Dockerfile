FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY ./target/spring-0.0.1-SNAPSHOT.jar /app
EXPOSE 8083
CMD ["java", "-jar", "spring-0.0.1-SNAPSHOT.jar"]