# Spring Cloud Task


## Jib

- **Build** the docker image locally

        gradle jibDockerBuild

- **Push** the image to the repository

        gradle jib

## Deployment

### Docker Compose

1. Use `gradle build` to create all the packages.
1. Run `gradle install` to publish maven artifact locally (using `maven` plugin)
   > Check generated artifact `com\example\batch-process\0.0.1-SNAPSHOT\batch-process-0.0.1-SNAPSHOT.jar`
1. Start docker compose to deploy all the services locally

    ```bash
    # Run docker clean to cleanup all the cache within docker engine
    ./docker-clean
    
    # Run docker compose to start all the services
    docker-compose up
   ```
    
1. Verify the connection to following services and database

    - [Minio Server](http://dockerhost:9001/minio/dataflow-bucket/)
    - [Rest Service Server](http://dockerhost:8080/departments/1)

1. Download and Start Data Flow Server locally

        # Download local dataflow server
        wget http://repo.spring.io/milestone/org/springframework/cloud/spring-cloud-dataflow-server-local/1.7.4.RELEASE/spring-cloud-dataflow-server-local-1.7.4.RELEASE.jar

        # Download command shell to use with datfalow
        wget http://repo.spring.io/milestone/org/springframework/cloud/spring-cloud-dataflow-shell/2.0.1.RELEASE/spring-cloud-dataflow-shell-2.0.1.RELEASE.jar

        # Launch dataflow server using the same postgreSQL connection previously deployed
        java -jar spring-cloud-dataflow-server-local-1.7.4.RELEASE.jar --spring.datasource.url=jdbc:postgresql://dockerhost:5432/dataflow --spring.datasource.username=postgres --spring.datasource.password=password --spring.datasource.driver-class-name=org.postgresql.Driver

1. Server runs at [Data flow server dashboard](http://localhost:9393/dashboard)

1. Add the task Application. `maven://com.example:batch-process:0.0.1-SNAPSHOT`
1. Crate Task from app definition (task type)
2. Using the dashboard is needed to pass the initial paremeters as key value pair
    
        --inputFile=dataflow-bucket:sample-data.zip
        --resourcesPath=dataflow-bucket 

### Kubernetes

#### Minikube

- Install `minikube` and `kubectl` following the instructions for the OS.
- Start `minikube`

   > Use settings to configure the memory, cpu, and other features.
   
        minikube start
    
- Verify the installation of `minikube`
    
        kubectl config current-context
        kubectl get nodes
        kubectl get pods,svc --all-namespaces

- Install `helm`

        # Install Helm
        curl https://raw.githubusercontent.com/helm/helm/master/scripts/get | bash
        # Init Helm
        helm init
        helm version
         
- Install `kompose`

        # Linux
        curl -L https://github.com/kubernetes/kompose/releases/download/v1.18.0/kompose-linux-amd64 -o kompose
        # macOS
        curl -L https://github.com/kubernetes/kompose/releases/download/v1.18.0/kompose-darwin-amd64 -o kompose
        
        chmod +x kompose
        sudo mv ./kompose /usr/local/bin/kompose
        
#### Kompose

Create the services needed to deploy the same initial services we have in previous environment using docker-compose`


#### Deployment

Deploy previous services and deployments

- Create the services, with the requirements for dataflow 

        kubectl apply -f .

- Create datasource and dependencies (except database, previously deployed)

        kubectl apply -f .

- Perform some migration and bootstrapping

        kubectl apply -f db-migration-job.yaml --force=true
        
- Verify pods are successfully deployed

        kubectl get pods,svc
        
- Check if dataflow dashboard is accessible
        
        http://localhost:32247/dashboard

#### Spring Batch DataFlow Task

In order to work are necessary some changes to be done.

- Use `Spring-cloud-deployer-kubernetes` as dependency instead using local.
- Use a docker image as the resource representing the worker. 
- Remove the passing of the environment variables to the worker (more on that in a moment).
- Check kubernetes is used for default launcher in [Spring data-flow server](http://localhost:32247/management/info)        


#### SETUP

- Create a new bucket into **minio**: `dataflow-bucket`
- Add file into previous bucket: `sample-data.zip`


#### Launching Task

- Perform a single test prior to launch the example to verify everything is working as expected

        app register --type task --name timestamp --uri docker:springcloudtask/timestamp-task:2.0.0.RELEASE --metadata-uri maven://org.springframework.cloud.task.app:timestamp-task:jar:metadata:2.0.0.RELEASE
        task create task1 --definition "timestamp"
        task launch task1

- Create a new application, using the generated docker image

        app register --type task --name batch-process-test --uri docker:jsa4000/dataflow-batch-process-k8s:0.0.1-SNAPSHOT
        task create task-test --definition "batch-process-test"
        task launch task-test
        
- Use the following parameters to launch the task

        --inputFile=dataflow-bucket:sample-data.zip
        --resourcesPath=dataflow-bucket 

- Destroy the task

        task destroy --name task-test
        
#### Known issues

- Too many connections in PostgreSQL

```sql
SELECT *
FROM   pg_settings
WHERE  name = 'max_connections';
```

```yml
version: '2'
services:
  postgres:
    image: postgres:10.3-alpine
    command: postgres -c 'max_connections=200'
    environment:
      POSTGRES_DB: pgdb
      POSTGRES_PASSWORD: postgres
      POSTGRES_USER: postgres
    stdin_open: true
    tty: true
    ports:
    - 5432:5432/tcp
WHERE  name = 'max_connections';
```    
     
#### References

- [Spring Cloud Deployer Kubernetes](https://github.com/spring-cloud/spring-cloud-deployer-kubernetes)
- [Routine Jobs with Kubernetes,](https://medium.com/pismolabs/routine-jobs-with-kubernetes-spring-cloud-dataflow-and-spring-cloud-task-d943bf107a8)