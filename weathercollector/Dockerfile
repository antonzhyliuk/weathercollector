FROM openjdk:16-alpine

RUN mkdir -p /app /app/resources

WORKDIR /app

COPY resources/migrations resources/migrations

COPY target/weathercollector-0.1.0-SNAPSHOT-standalone.jar .

CMD java -jar weathercollector-0.1.0-SNAPSHOT-standalone.jar server
