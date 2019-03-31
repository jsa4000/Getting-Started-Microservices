# Spring Cloud Task


## Jib

- **Build** the docker image locally

        gradle jibDockerBuild

- **Push** the image to the repository

        gradle jib

## Deployment

### Docker Compose

- [Minio Server](http://dockerhost:9001/minio/dataflow-bucket/)
- [Rest Service Server](http://dockerhost:8080/departments/1)