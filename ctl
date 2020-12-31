#/bin/sh

docker-compose exec weathercollector java -jar weathercollector-0.1.0-SNAPSHOT-standalone.jar $1
