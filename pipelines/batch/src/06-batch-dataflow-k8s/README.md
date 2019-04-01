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
        
#### Kommpose

Create the services needed to deploy the same initial services we have in previous environment using docker-compose`

``
