FROM eclipse-temurin:11-jre
COPY target/*.jar app.jar
EXPOSE 10000
ENTRYPOINT ["java", "-jar", "/app.jar"]
