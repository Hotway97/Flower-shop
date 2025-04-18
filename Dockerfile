FROM openjdk:21-jdk

WORKDIR /app

COPY pom.xml .
COPY src ./src

COPY target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]