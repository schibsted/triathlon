[![Build Status](https://travis-ci.org/schibsted/triathlon.svg)](https://travis-ci.org/schibsted/triathlon)
# Triathlon

## A Marathon wrapper for distributed Mesos cluster selection

This is an `rxnetty` server that will register itself to an `Eureka2` cluster. It also will receive from Eureka2 all updates about the Marathon services registered on this Eureka.

Using the endpoint `/v2/apps` you can send via `POST` an [App definition JSON](https://mesosphere.github.io/marathon/docs/native-docker.html) like the one that Marathon expects.

Triathlon then will look for a field `constraints` in the document with the parameter `datacenter`. If it is found will look for a marathon running in a datacenter with the same name and will forward the JSON document to it. Finally will return the response from marathon.

### Implemented constraints operator

We had wrapped the following constraints operators: (see the [Marathon documentation](https://github.com/mesosphere/marathon/blob/master/docs/docs/constraints.md) for more info)

#### UNIQUE operator

With the `UNIQUE` operator we ensure that we only deploy one instance of our application on each datacenter. For example the following command only will deploy 3 instances if we have almost 3 datacenters.

```
$ curl -X POST -H "Content-type: application/json" localhost:9090/v2/apps -d '{
    "container": { ... }
    "id": "my-app",
    "instances": 3,
    "constraints": [["datacenter", "UNIQUE"]]
}'
```

#### CLUSTER operator

Using the `CLUSTER` operator we can deploy all our instances to the same datacenter. The following example will deploy all 3 instances on the datacenter `pluto-dc`:

```
$ curl -X POST -H "Content-type: application/json" localhost:9090/v2/apps -d '{
    "container": { ... }
    "id": "my-app",
    "instances": 3,
    "constraints": [["datacenter", "CLUSTER", "pluto-dc"]]
}'
```

#### GROUP_BY operator

The `GROUP_BY` operator can be used to distribute applications evenly across all our datacenters. The following example will deploy 2 instances on each datacenter assuming that we have two datacenters:

```
$ curl -X POST -H "Content-type: application/json" localhost:9090/v2/apps -d '{
    "container": { ... }
    "id": "my-app",
    "instances": 4,
    "constraints": [["datacenter", "GROUP_BY"]]
}'
```

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
