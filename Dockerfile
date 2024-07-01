FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/mentor-mate-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]


# FROM openjdk:17-alpine
# WORKDIR /opt
# ENV PORT 8080
# EXPOSE 8080
# COPY target/*.jar /opt/app.jar
# ENTRYPOINT exec java $JAVA_OPTS -jar app.jar

