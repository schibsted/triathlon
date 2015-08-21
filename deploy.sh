#!/bin/bash

set -e

./gradlew distTar

MARATHON=marathon_gce

curl -X DELETE -H "Content-Type: application/json" http://${MARATHON}:8080/v2/apps/triathlon

sleep 3

VERSION=$(docker images docker-registry-hostname/triathlon | awk '{print $2}' | grep -v TAG | sort -n | tail -1)
VERSION=$(($VERSION+1))

docker build -t docker-registry-hostname/triathlon:${VERSION} .
docker push docker-registry-hostname/triathlon:${VERSION}

cat misc/template.json | sed "s/VERSION/$VERSION/" > run.json

curl -X POST -H "Content-Type: application/json" http://${MARATHON}:8080/v2/apps -d@run.json

rm run.json

sleep 5

curl -X GET -H "Content-Type: application/json" http://${MARATHON}:8080/v2/apps/triathlon | python -m json.tool
HOST=$(curl -X GET -H "Content-Type: application/json" http://${MARATHON}:8080/v2/apps/triathlon | python -m json.tool | grep host | cut -f4 -d'"')

echo "curl -X POST -H \"Content-Type: application/json\" http://${HOST}:9090/v2/apps -d@misc/test.json -v"

