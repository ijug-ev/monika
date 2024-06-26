# 👱🏻‍♀️ Monika: The iJUG's homebrewn Infrastructure Monitor

*Monika* is a Java-implemented RESTful Web Service serving as an agent for Uptime Kuma to read the local disk's free space.


## Use

* Disk Free: `/monika/status` responds status `100` on hardware failures, status `901` if volume has less than 20% free, status `2XX` in all other cases (where `XX` is the lowest free value in %). The status text contains the smallest free percentage of all monitored volumes. Example: `260 Smallest free volume is 60 %.`
* Health Check: `/monika/health` responds status 204 always.


## Administrate

* `docker logs monika` - Contains one entry for each monitored path per `/status` request, like:
  ```
  /home is 60 % free
  /var is 75 % free
  ```


## Deploy

* `docker run --name monika -d -e MONITORED_PATHS=/home:/var -e IP_PORT=8123 -P ghcr.io/ijug-ev/monika`: Monika will listen to requests on port 8123 and monitor paths `/home` and `/var`.
  - `MONITORED_PATH`: Monika will monitor each path in this list. The default is `/`, i. e. only the root file system is monitored.
  - `IP_PORT`: Monika will listen to this IP port. The default is `8080`.


## Build

`./build`, i. e.:
```bash
#!/bin/bash
mvn clean package
cp src/main/docker/Dockerfile target
pushd ./target
docker build -t monika .
popd
docker run -it --rm -P monika
```
