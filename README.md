# jaws-admin-gui [![Java CI with Gradle](https://github.com/JeffersonLab/jaws-admin-gui/actions/workflows/ci.yml/badge.svg)](https://github.com/JeffersonLab/jaws-admin-gui/actions/workflows/ci.yml) [![Docker](https://img.shields.io/docker/v/jeffersonlab/jaws-admin-gui?sort=semver&label=DockerHub)](https://hub.docker.com/r/jeffersonlab/jaws-admin-gui)
Web Admin interface for [JAWS](https://github.com/JeffersonLab/jaws) to manage alarm registrations and view notifications.

<p>
<a href="#"><img src="https://github.com/JeffersonLab/jaws-admin-gui/raw/main/src/main/webapp/resources/img/screenshot1.png"/></a>     
</p>

---
 - [Overview](https://github.com/JeffersonLab/jaws-admin-gui#overview)
 - [Quick Start with Compose](https://github.com/JeffersonLab/jaws-admin-gui#quick-start-with-compose) 
 - [Install](https://github.com/JeffersonLab/jaws-admin-gui#install)
 - [Configure](https://github.com/JeffersonLab/jaws-admin-gui#configure)
 - [Build](https://github.com/JeffersonLab/jaws-admin-gui#build)
 - [Release](https://github.com/JeffersonLab/jaws-admin-gui#release)
 - [See Also](https://github.com/JeffersonLab/jaws-admin-gui#see-also)
---

## Overview
Alarm system registration data consists of locations, categories, classes, and instances.  Collectively the data forms effective registrations.

## Quick Start with Compose
1. Grab project
```
git clone https://github.com/JeffersonLab/jaws-admin-gui
cd jaws-admin-gui
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

   1. Install service [dependencies](https://github.com/JeffersonLab/jaws-admin-gui/blob/main/deps.yml)
   1. Download [Wildfly 26.1.3](https://www.wildfly.org/downloads/)
   1. [Configure](https://github.com/JeffersonLab/jaws-admin-gui#configure) Wildfly and start it
   1. Download [jaws.war](https://github.com/JeffersonLab/jaws-admin-gui/releases) and deploy it to Wildfly
   1. Navigate your web browser to localhost:8080/jaws


## Configure

### Configtime
Wildfly must be pre-configured before the first deployment of the app. The [wildfly bash scripts](https://github.com/JeffersonLab/wildfly#configure) can be used to accomplish this. See the [Dockerfile](https://github.com/JeffersonLab/jaws-admin-gui/blob/main/Dockerfile) for an example.

### Runtime
The following environment variables are required:

| Name | Description |
|----------|---------|
| BOOTSTRAP_SERVERS | Host and port pair pointing to a Kafka server to bootstrap the client connection to a Kafka Cluser; example: `kafka:9092` |
| SCHEMA_REGISTRY | URL to Confluent Schema Registry; example: `http://registry:8081` |

## Build
This project is built with [Java 17](https://adoptium.net/) (compiled to Java 11 bytecode), and uses the [Gradle 7](https://gradle.org/) build tool to automatically download dependencies and build the project from source:

```
git clone https://github.com/JeffersonLab/jaws-admin-gui
cd jaws-admin-gui
gradlew build
```
**Note**: If you do not already have Gradle installed, it will be installed automatically by the wrapper script included in the source

**Note for JLab On-Site Users**: Jefferson Lab has an intercepting [proxy](https://gist.github.com/slominskir/92c25a033db93a90184a5994e71d0b78)

**See**: [Docker Development Quick Reference](https://gist.github.com/slominskir/a7da801e8259f5974c978f9c3091d52c#development-quick-reference)

## Release
1. Bump the version number in build.gradle and commit and push to GitHub (using [Semantic Versioning](https://semver.org/)).
2. Create a new release on the GitHub Releases page corresponding to the same version in the build.gradle.   The release should enumerate changes and link issues.   A war artifact can be attached to the release to facilitate easy install by users.
3. [Publish to DockerHub](https://github.com/JeffersonLab/jaws-admin-gui/actions/workflows/docker-publish.yml) GitHub Action should run automatically.
4. Bump and commit quick start [image version](https://github.com/JeffersonLab/jaws-admin-gui/blob/main/docker-compose.override.yml)

## See Also
- [JLab alarm data](https://github.com/JeffersonLab/alarms)
- [Developer Notes](https://github.com/JeffersonLab/jaws-admin-gui/wiki/Developer-Notes)
