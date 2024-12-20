# jaws-web [![Java CI with Gradle](https://github.com/JeffersonLab/jaws-web/actions/workflows/ci.yaml/badge.svg)](https://github.com/JeffersonLab/jaws-web/actions/workflows/ci.yaml) [![Docker](https://img.shields.io/docker/v/jeffersonlab/jaws-web?sort=semver&label=DockerHub)](https://hub.docker.com/r/jeffersonlab/jaws-web)
A [Java EE 8](https://en.wikipedia.org/wiki/Jakarta_EE) web application for managing [JAWS](https://github.com/JeffersonLab/jaws) inventory and notifications built with the [Smoothness](https://github.com/JeffersonLab/smoothness) web template.

<p>
<a href="#"><img src="https://github.com/JeffersonLab/jaws-web/raw/main/Screenshot.png"/></a>     
</p>

---
 - [Overview](https://github.com/JeffersonLab/jaws-web#overview)
 - [Quick Start with Compose](https://github.com/JeffersonLab/jaws-web#quick-start-with-compose) 
 - [Install](https://github.com/JeffersonLab/jaws-web#install)
 - [Configure](https://github.com/JeffersonLab/jaws-web#configure)
 - [Build](https://github.com/JeffersonLab/jaws-web#build)
 - [Develop](https://github.com/JeffersonLab/jaws-web#develop)
 - [Release](https://github.com/JeffersonLab/jaws-web#release)
 - [Deploy](https://github.com/JeffersonLab/jaws-web#deploy) 
 - [See Also](https://github.com/JeffersonLab/jaws-web#see-also)
---

## Overview
This web app provides a user interface to JAWS.  Operators can view alarm notifications by Location and System and apply overrides.   Admins can manage the master alarm database (inventory of alarms).  Users can also view reports.

## Quick Start with Compose
1. Grab project
```
git clone https://github.com/JeffersonLab/jaws-web
cd jaws-web
```
2. Launch [Compose](https://github.com/docker/compose)
```
docker compose up
```
3. Launch web browser
```
http://localhost:8080/jaws
```
**Note**: Login with demo username "tbrown" and password "password".

**Note**: The docker-compose services require significant system resources - tested with 4 CPUs and 4GB memory.

**See**: [Docker Compose Strategy](https://gist.github.com/slominskir/a7da801e8259f5974c978f9c3091d52c)

## Install
This application requires a Java 11+ JVM and standard library to run, plus a Java EE 8+ application server (developed with Wildfly).

   1. Install service [dependencies](https://github.com/JeffersonLab/jaws-web/blob/main/deps.yaml)
   1. Download [Wildfly 26.1.3](https://www.wildfly.org/downloads/)
   1. [Configure](https://github.com/JeffersonLab/jaws-web#configure) Wildfly and start it
   1. Download [jaws.war](https://github.com/JeffersonLab/jaws-web/releases) and deploy it to Wildfly
   1. Navigate your web browser to localhost:8080/jaws


## Configure

### Configtime
Wildfly must be pre-configured before the first deployment of the app. The [wildfly bash scripts](https://github.com/JeffersonLab/wildfly#configure) can be used to accomplish this. See the [Dockerfile](https://github.com/JeffersonLab/jaws-web/blob/main/Dockerfile) for an example.

### Runtime
The following environment variables are required:

| Name | Description |
|----------|---------|
| BOOTSTRAP_SERVERS | Host and port pair pointing to a Kafka server to bootstrap the client connection to a Kafka Cluser; example: `kafka:9092` |
| SCHEMA_REGISTRY | URL to Confluent Schema Registry; example: `http://registry:8081` |

## Build
This project is built with [Java 17](https://adoptium.net/) (compiled to Java 11 bytecode), and uses the [Gradle 7](https://gradle.org/) build tool to automatically download dependencies and build the project from source:

```
git clone https://github.com/JeffersonLab/jaws-web
cd jaws-web
gradlew build
```
**Note**: If you do not already have Gradle installed, it will be installed automatically by the wrapper script included in the source

**Note for JLab On-Site Users**: Jefferson Lab has an intercepting [proxy](https://gist.github.com/slominskir/92c25a033db93a90184a5994e71d0b78)

**See**: [Docker Development Quick Reference](https://gist.github.com/slominskir/a7da801e8259f5974c978f9c3091d52c#development-quick-reference)

## Develop
In order to iterate rapidly when making changes it's often useful to run the app directly on the local workstation, perhaps leveraging an IDE.  In this scenario run the service dependencies with:
```
docker compose -f deps.yaml up
# OR if on JLab network use control system config with `jlab-deps.yaml` instead.
```
**Note**: The local install of Wildfly should be [configured](https://github.com/JeffersonLab/jaws-web#configure) to proxy connections to services via localhost and therefore the environment variables should contain:
```
KEYCLOAK_BACKEND_SERVER_URL=http://localhost:8081
FRONTEND_SERVER_URL=https://localhost:8443
```
Further, the local DataSource must also leverage localhost port forwarding so the `standalone.xml` connection-url field should be: `jdbc:oracle:thin:@//localhost:1521/xepdb1`.  

The [server](https://github.com/JeffersonLab/wildfly/blob/main/scripts/server-setup.sh) and [app](https://github.com/JeffersonLab/wildfly/blob/main/scripts/app-setup.sh) setup scripts can be used to setup a local instance of Wildfly. 

## Release
1. Bump the version number in the VERSION file and commit and push to GitHub (using [Semantic Versioning](https://semver.org/)).
2. The [CD](https://github.com/JeffersonLab/jaws-web/blob/main/.github/workflows/cd.yaml) GitHub Action should run automatically invoking:
    - The [Create release](https://github.com/JeffersonLab/java-workflows/blob/main/.github/workflows/gh-release.yaml) GitHub Action to tag the source and create release notes summarizing any pull requests.   Edit the release notes to add any missing details.  A war file artifact is attached to the release.
    - The [Publish docker image](https://github.com/JeffersonLab/container-workflows/blob/main/.github/workflows/docker-publish.yaml) GitHub Action to create a new demo Docker image.

## Deploy
At JLab this app is found at [ace.jlab.org/jaws](https://ace.jlab.org/jaws) and internally at [acctest.acc.jlab.org/jaws](https://acctest.acc.jlab.org/jaws).  However, those servers are proxies for `jaws.acc.jlab.org` and `jawstest.acc.jlab.org` respectively.  This app makes up one service in a set of services defined in a compose file that make up the JAWS system and deployments are managed by [JAWS](https://github.com/JeffersonLab/jaws).

## See Also
- [User Guide](https://github.com/JeffersonLab/jaws-web/wiki/User-Guide)
- [JLab alarm data](https://github.com/JeffersonLab/alarms)
