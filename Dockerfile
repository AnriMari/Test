FROM openjdk:17-jdk-slim

WORKDIR /app
COPY target/spring-boot_security-demo-0.0.1-SNAPSHOT.jar /app/myapp.jar
CMD ["java", "-jar", "myapp.jar"]