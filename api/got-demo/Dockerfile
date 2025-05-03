FROM amazoncorretto:17-alpine-jdk

WORKDIR /app

COPY . .

RUN ./gradlew clean build

EXPOSE 8080

CMD ["java", "-jar", "./build/libs/got-demo-0.0.1-SNAPSHOT.jar"]


