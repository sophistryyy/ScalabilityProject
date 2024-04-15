# Filename: Dockerfile
FROM maven:latest
ENV JAVA_HOME=/opt/java/openjdk
COPY --from=eclipse-temurin:21 $JAVA_HOME $JAVA_HOME
ENV PATH="${JAVA_HOME}/bin:${PATH}"
WORKDIR /usr/src/app
COPY ../parent-pom.xml .

WORKDIR /usr/src/app/project
COPY ../execution-service .

CMD ["mvn", "spring-boot:run"]