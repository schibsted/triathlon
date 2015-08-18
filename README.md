[![Build Status](https://travis.schibsted.io/spt-infrastructure/triathlon.svg?token=zyJNyfy6QrXmmKaFXRiZ&branch=master)](https://travis.schibsted.io/spt-infrastructure/triathlon)
# Triathlon

## A Marathon wrapper for distributed Mesos cluster selection

This is an `rxnetty` server that will register itself to an `Eureka2` cluster. It also will receive from Eureka2 all updates about the Marathon services registered on this Eureka.

Using the endpoint `/v2/apps` you can send via `POST` an [App definition JSON](https://mesosphere.github.io/marathon/docs/native-docker.html) like the one that Marathon expects.

Triathlon then will look for a field `constraints` in the document with the parameter `datacenter`. If it is found will look for a marathon running in a datacenter with the same name and will forward the JSON document to it. Finally will return the response from marathon.

## Deploy to Marathon

```
./gradlew distTar
docker build -t ${DOCKER_REGISTRY}/triathlon .
docker push ${DOCKER_REGISTRY}/triathlon
curl -X POST -H "Content-Type: application/json" http://${MARATHON}/v2/apps -d@misc/triathlon.json
```

### Docker registry certificate

We need to copy the certificate (`ca.crt`) into `/etc/docker/certs.d/mesosprototype-docker-registry.infra-dev.schibsted.io\:5000`

### Deploy from OSX

If you are running OSX we recommend to push the container to Docker Registry using a VM that you can build using the provided `Vagrantfile`. Install `Vagrant` and simply do a `vagrant up`. It should takes care of installing `docker` and copying the `ca.crt` from `./misc` to the correct location.

### License

Copyright 2015 Schibsted Products and Technology

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
