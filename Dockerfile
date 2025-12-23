FROM eclipse-temurin:21-jdk-alpine
LABEL maintainer="TejaratBank Core Team"
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]