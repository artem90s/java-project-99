FROM gradle:8.10.2-jdk21
WORKDIR /app
COPY . .
RUN gradle bootJar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "build/libs/app-0.0.1-SNAPSHOT.jar"]
