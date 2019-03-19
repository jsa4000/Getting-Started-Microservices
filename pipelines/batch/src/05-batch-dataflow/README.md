# Spring Cloud Task

## Usage

1. Use `gradle build` to create all the packages.
1. Then deploy the `docker-compose` file inside the project.

## Dataflow Server (local)

- Download the Spring Cloud Data Flow Server and Shell apps:

    wget http://repo.spring.io/milestone/org/springframework/cloud/spring-cloud-dataflow-server-local/1.7.4.RELEASE/spring-cloud-dataflow-server-local-1.7.4.RELEASE.jar

    wget http://repo.spring.io/milestone/org/springframework/cloud/spring-cloud-dataflow-shell/2.0.1.RELEASE/spring-cloud-dataflow-shell-2.0.1.RELEASE.jar

- Launch the Data Flow Server

> Since the Data Flow Server is a Spring Boot application, you can run it just by using java -jar.

    java -jar spring-cloud-dataflow-server-local-1.7.4.RELEASE.jar

    java -jar C:\Users\jsantosa\Downloads\spring-cloud-dataflow-server-local-1.7.4.RELEASE.jar

> Server runs at http://localhost:9393

- Launch the shell:

    java -jar spring-cloud-dataflow-shell-2.0.1.RELEASE.jar

    java -jar C:\Users\jsantosa\Downloads\spring-cloud-dataflow-shell-2.0.1.RELEASE.jar

> If the Data Flow Server and shell are not running on the same host, point the shell to the Data Flow server URL:

    server-unknown:>dataflow config server http://localhost
    Successfully targeted http://localhost
    dataflow:>



## References

[Deploying Spring Cloud Data Flow Local Server](https://docs.spring.io/spring-cloud-dataflow/docs/1.2.0.M1/reference/html/getting-started-deploying-spring-cloud-dataflow.html)