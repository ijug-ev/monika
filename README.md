# 👱🏻‍♀️ Monika: The iJUG's homebrewn Infrastructure Monitor

*Monika* is a Java-implemented RESTful Web Service serving as an agent for Uptime Kuma to read the local disk's free space.


## Use

* Disk Free: `/question/status` responds status 100 on hardware failures, status 901 if volume has less than 20% free, status 2XX in all other cases (where XX is the lowest free value in %).
* Health Check: `/question/health` responds status 200 always.


## Deploy

* Use latest [Docker image](https://github.com/ijug-ev/monika/blob/master/src/main/docker/Dockerfile). **We're currently setting up Github Actions to deploy Monika's image into the [Github Docker Repository](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-container-registry)**


## Build

`./build`, i. e.:
```bash
#!/bin/bash
mvn clean package
cp src/main/docker/Dockerfile target
pushd ./target
docker build -t helloworld .
popd
docker run -it --rm -P helloworld
```
