FROM openjdk:17-slim

EXPOSE 8080

COPY build/libs/*.jar estore.jar

ENTRYPOINT ["java", "-jar", "estore.jar"]