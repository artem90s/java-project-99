FROM gradle:8.6-jdk21
WORKDIR /app
COPY . .
RUN gradle bootJar
EXPOSE 8080
CMD java -jar build/libs/app-0.0.1-SNAPSHOT.jar
