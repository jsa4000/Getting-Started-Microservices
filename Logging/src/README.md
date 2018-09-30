# Installation

## Requeriments

- Java JDK 8
- Gradle +4

## Installing gradle

- Download desired gradle release version from gradle website

        wget https://services.gradle.org/distributions/gradle-4.10.2-bin.zip

- EXtract the content to a folder to store multiple version (if needed)

        mkdir /opt/gradle
        unzip -d /opt/gradle gradle-4.10.2-bin.zip

- Check the binaries have been extracted properly

        ls /opt/gradle/gradle-4.10.2

- Set environment variable permanently

        # Append following line to /etc/profile
        export PATH=$PATH:/opt/gradle/gradle-4.10.2/bin

- Verify gradle version

        gradle -v

## Build Solution

### Build and Create DockerFile definitions using Jib

- Set into root folder

        cd /vagrant/files/tracing/src

- Run the following commands from command line

> Gradle setting for the solution has been set to parallel so all the projects will be build using multi-threading

        # Clean the project. If there are locked files use: ``rm -R tracingLib\build instead``
        gradle clean

        # Build the project
        gradle build

        # Create Dockerfile using Jib
        gradle jibExportDockerContext

### Create Docker images using Jib

    gradle jibDockerBuild

## Exectute

- Build images (force to rebuild)

    sudo docker-compose -f docker-compose-servers.yml build

- Execute (detach mode) servers and tracers separetely

    sudo docker-compose -f docker-compose-tracers.yml up -d
    sudo docker-compose -f docker-compose-loggers.yml up -d
    sudo docker-compose -f docker-compose-servers.yml up -d

- Verify all image and container have been build and they are running

    sudo docker image list
    sudo docker stats

If getting some error during the bootstrapping use the following command previously (probably because no previous gracefully shutdown)

        sudo docker-compose -f docker-compose-tracers.yml rm

### Shutdown

     sudo docker-compose -f docker-compose-servers.yml down --remove-orphans

## Tracing

- [API GAteway](http://10.0.0.10:8080/swagger-ui.html)
- [Jaeger Dashboard](http://10.0.0.10:16686)
- [Zipkin Dashboard](http://10.0.0.10:9412)

## Logging

- [Kibana Dashboard](http://10.0.0.10:5601)

s> Please go to http://localhost:5601/ with your browser. Then, you need to set up the index name pattern for Kibana. Please specify ``fluentd-*`` to ``Index name or pattern`` and press **Create** button. Then, go to **Discover** tab to seek for the logs. As you can see, logs are properly collected into Elasticsearch + Kibana, via Fluentd.
