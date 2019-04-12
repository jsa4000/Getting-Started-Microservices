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

1. Download and Start Data Flow Server locally (if not deployed in docker-compose)

        # Download local dataflow server
        wget http://repo.spring.io/milestone/org/springframework/cloud/spring-cloud-dataflow-server-local/1.7.4.RELEASE/spring-cloud-dataflow-server-local-1.7.4.RELEASE.jar
        ## There is no jar for versions > 1.7.4, compile github or use docker version (local) instead

        # Download command shell to use with datfalow
        wget http://repo.spring.io/milestone/org/springframework/cloud/spring-cloud-dataflow-shell/1.7.4.RELEASE/spring-cloud-dataflow-shell-1.7.4.RELEASE.jar
        wget http://repo.spring.io/milestone/org/springframework/cloud/spring-cloud-dataflow-shell/2.0.1.RELEASE/spring-cloud-dataflow-shell-2.0.1.RELEASE.jar

        # Launch dataflow server using the same postgreSQL connection previously deployed
        java -jar spring-cloud-dataflow-server-local-1.7.4.RELEASE.jar --spring.datasource.url=jdbc:postgresql://dockerhost:5432/dataflow --spring.datasource.username=postgres --spring.datasource.password=password --spring.datasource.driver-class-name=org.postgresql.Driver --spring.cloud.dataflow.server.uri=http://dockerhost:9393

        # Launch the integrated shell
        java -jar spring-cloud-dataflow-shell-2.0.1.RELEASE.jar --dataflow.uri=http://dockerhost:9393
        docker exec -it dataflow-server java -jar shell.jar

1. Server runs at [Data flow server dashboard](http://localhost:9393/dashboard)

1. Add the task Applications:

   - maven://com.example:batch-process:0.0.1-SNAPSHOT
   - maven://com.example:task-notifier:0.0.1-SNAPSHOT
   - maven://com.example:batch-uploader-k8s:0.0.1-SNAPSHOT
   - maven://com.example:batch-process-prod-k8s:0.0.1-SNAPSHOT
  
   ```bash
    app register --name composed-task-runner --type task --uri maven://org.springframework.cloud.task.app:composedtaskrunner-task:2.1.0.RELEASE
    app register --name batch-process-app --type task --uri maven://com.example:batch-process:0.0.1-SNAPSHOT
    app register --name task-notifier-app --type task --uri maven://com.example:task-notifier:0.0.1-SNAPSHOT
    app register --name batch-uploader-app --type task --uri maven://com.example:batch-uploader-k8s:0.0.1-SNAPSHOT
    app register --name batch-process-prod-k8s-app --type task --uri maven://com.example:batch-process-prod-k8s:0.0.1-SNAPSHOT
  
    app list
    ```
    
1. Crate Task from app definition (task type)

    ```bash
    task create --name batch-process-task --definition "batch-process-app"
    task create --name task-notifier-task --definition "task-notifier-app"
    task create --name batch-uploader-task --definition "batch-uploader-app"
    task create --name batch-process-prod-k8s-task --definition "batch-process-prod-k8s-app"
    ```bash

1. Using the dashboard is needed to pass the initial paremeters as key value pair
    
        --inputFile=dataflow-bucket:sample-data.zip
        --resourcesPath=dataflow-bucket
                
1. Launch the task

         # Using local server (jar)
         task launch --name batch-process-task --arguments "--inputFile=dataflow-bucket:sample-data.zip --resourcesPath=dataflow-bucket"
         # Using docker
         task launch --name batch-process-task --arguments "--inputFile=dataflow-bucket:sample-data.zip --resourcesPath=dataflow-bucket --spring.profiles.active=docker,master"
         
         task launch --name batch-uploader-task --arguments "--spring.profiles.active=docker,master"
         
         # Using docker and local deployer compilatation
         task launch --name batch-process-prod-k8s-task --arguments "--inputFile=dataflow-bucket:sample-data-prod.zip --resourcesPath=dataflow-bucket --spring.profiles.active=docker,master"
            
1. Add following app inside Spring Data-flow server, to support composite tasks

    - Name: composed-task-runner	
    - Type: task
    - Maven: maven://org.springframework.cloud.task.app:composedtaskrunner-task:2.1.0.RELEASE

    
    task create composite-task --definition "batch-process-app --spring.profiles.active=docker,master && batch-uploader-app"
        
    task launch --name composite-task --arguments "--inputFile=dataflow-bucket:sample-data.zip --resourcesPath=dataflow-bucket"
                      
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

- Create the services, with the requirements for dataflow (`deployments/k8s/services`)

        kubectl apply -f .

- Create dataflow, roles and dependencies (`deployments/k8s/dataflow`)

        kubectl apply -f .

- Perform some migration and bootstrapping (`deployments/k8s/services/migration`)

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

- Connect to the minio server at [http://localhost:31680/minio/](http://localhost:31680/minio/)
- Create a new bucket into **minio**: `dataflow-bucket`
- Add following files at `/deployments/compose/files` into previous bucket: `dataflow-bucket`
    - `sample-data.zip`
    - `sample-data-prod.zip`

#### Launching Task

- Connect to dataflow server via shell or dashboard

    ```bash
    # Using pods alterady deployed (use 'kubectl get pods' to get the pod id)
    kubectl exec scdf-server-58cb976466-9gqrv -it -- java -jar shell.jar
    # Since the pods is deployed in port 80, override default configuration
    server-unknown:> dataflow config server http://localhost:80
    # Or Simply join the previous two statements
    kubectl exec scdf-server-6fcd7bf4c-ggshs -it -- java -jar shell.jar --dataflow.uri=http://localhost:80
    
    # Using compiled version locally (recommended in production environments)
    java -jar spring-cloud-dataflow-shell-2.0.1.RELEASE.jar --dataflow.uri=http://dockerhost:9393
    ```
    
- Perform a single test prior to launch the example to verify everything is working as expected

    ```bash
    app register --type task --name timestamp --uri docker:springcloudtask/timestamp-task:2.0.0.RELEASE --metadata-uri maven://org.springframework.cloud.task.app:timestamp-task:jar:metadata:2.0.0.RELEASE
    task create task1 --definition "timestamp"
    task launch task1
    
    # Get the result
    task execution list
    ```

- Create a new application, using the generated docker image

    ```bash
    # Resgister apps
    app register --type task --name composed-task-runner --uri docker:springcloudtask/composedtaskrunner-task:2.1.0.RELEASE
    app register --type task --name batch-process-app --uri docker:jsa4000/dataflow-batch-process-k8s:0.0.1-SNAPSHOT
    app register --type task --name batch-uploader-app --uri docker:jsa4000/dataflow-batch-uploader-k8s:0.0.1-SNAPSHOT
    app register --type task --name notifier-app --uri docker:jsa4000/dataflow-task-notifier:0.0.1-SNAPSHOT
    app register --type task --name batch-process-prod-app --uri docker:jsa4000/dataflow-batch-process-prod-k8s:0.0.1-SNAPSHOT
    
    app list
    
    # Create task with previous app
    task create batch-process-task --definition "batch-process-app"
    task create batch-uploader-task --definition "batch-uploader-app"
    task create notifier-task --definition "notifier-app"
    task create batch-process-prod-task --definition "batch-process-prod-app"
      
    # Launch task individually
    task launch notifier-task --arguments "--mail.auth.username= --mail.auth.password="
    task launch batch-uploader-task --arguments "--spring.profiles.active=k8s,master"
    task launch batch-process-task --arguments "--spring.profiles.active=k8s,master --inputFile=dataflow-bucket:sample-data.zip --resourcesPath=dataflow-bucket"
    task launch batch-process-prod-task --arguments "--spring.profiles.active=k8s,master --inputFile=dataflow-bucket:sample-data-prod.zip --resourcesPath=dataflow-bucket/sample-data-prod"

    # Get the result
    task execution list
    job execution list
    job execution display --id 1
    ```
       
- Use the following parameters to launch the task (`create batch-process-task`)

  > Parameter `--resourcesPath` does not work the same way in batch-process-PP and  batch-process-prod-app, since folder are not yet implemented in the first project.

        --inputFile=dataflow-bucket:sample-data.zip
        --resourcesPath=dataflow-bucket 
        --batch.max-workers=8
        
        --inputFile=dataflow-bucket:sample-data.zip --resourcesPath=dataflow-bucket

        # Create and upload the data to test 'sample-data-test.zip'
        task launch batch-process-prod-task --arguments "--batch.max-workers=1 --spring.profiles.active=k8s,master --inputFile=dataflow-bucket:sample-data-test.zip --resourcesPath=dataflow-bucket/sample-test-prod"

- Destroy the task

        task destroy --name task-test
        
- Check resources in kubernetes

        kubectl get svc,pods,job,deployments
        
- Remove completed Jobs/Pods

        kubectl get jobs | awk '$4 ~ /[2-9]d$/ || $3 ~ 1' | awk '{print $1}' | xargs kubectl delete job        
        kubectl get pods | awk '$2 ~ 0/1 && $3 ~ "Completed"' | awk '{print $1}' | xargs kubectl delete pod
        kubectl get pods | awk '$2 ~ 0/1 && $3 ~ "Error"' | awk '{print $1}' | xargs kubectl delete pod
        
#### Benchmark

- Create the test data (`csv-generator`). Configure the number of records desired to be generated for the test and execute the app
    
    java -jar csv-genrator-0.0.1-SNAPSHOT.jar --pathString=/tmp/sample-data-test.csv --count=1000000

- Split the files into chunks

        docker run -it -v /tmp/test:/tmp/test busybox /bin/sh
        
        cd /tmp/test
        
        # Be care about the files to be generated from total lines in the original file. (if partitions > `zz`, then use `-a 3` or more resolution)
        time split -l 100000 -a 2 sample-data.csv sample-data-
        
        for file in *; do mv "$file" "${file%}.csv"; done
        
- Compress the file using zip and renamed to `sample-data-test.zip`

- Upload the file to `minio` server.

- Create the app and the task

        app register --type task --name batch-process-prod-app --uri docker:jsa4000/dataflow-batch-process-prod-k8s:0.0.1-SNAPSHOT
        
        task create batch-process-prod-task --definition "batch-process-prod-app"
        
- Launch the task previously defined and with the commands (`00:28:02.359`)

        # Create and upload the data to test 'sample-data-test.zip'
        task launch batch-process-prod-task --arguments "--batch.max-workers=1 --spring.profiles.active=k8s,master --inputFile=dataflow-bucket:sample-data-test.zip --resourcesPath=dataflow-bucket/sample-test-prod"

- Launch the task previously defined and with the commands (`00:15:33.025`)    

        # Create and upload the data to test 'sample-data-test.zip'
        task launch batch-process-prod-task --arguments "--batch.max-workers=8 --spring.profiles.active=k8s,master --inputFile=dataflow-bucket:sample-data-test.zip --resourcesPath=dataflow-bucket/sample-test-prod"

- Compare both results

#### Composed Tasks

Spring Cloud Data Flow allows a user to create a directed graph where each node of the graph is a task application. This is done by using the DSL for composed tasks. A composed task can be created via the RESTful API, the Spring Cloud Data Flow Shell, or the Spring Cloud Data Flow UI.

![Parameters to configure tasks](images/composite-tasks.png)   

Out of the box the Composed Task Runner application is not registered with Spring Cloud Data Flow. So, to launch composed tasks we must first register the Composed Task Runner as an application with Spring Cloud Data Flow as follows:

Firstly add following app inside Spring Data-flow server;

- Name: composed-task-runner	
- Type: task
- Docker: docker:springcloudtask/composedtaskrunner-task:2.1.0.RELEASE
- [LOCAL] Maven: maven://org.springframework.cloud.task.app:composedtaskrunner-task:2.1.0.RELEASE

Create the composition task from the previous two already created, plus the `composed-task-runner`

    Importation-Node: import-batch-app 'FAILED'->Notifier-fail: notifier-app 'COMPLETED'->notifier-true: notifier-app 

Launch the task with the following arguments:
 
 > It must be **specified** the URL where data-flow server is located. 
 
    task launch --name composed-task --arguments "--dataflow-server-uri=http://scdf-server.default.svc.cluster.local:80"

 > It can be specified also at server side within the env variable `SPRING_CLOUD_DATAFLOW_SERVER_URI`
   
Also, specify other values specific for the inputs:

        --inputFile=dataflow-bucket:sample-data.zip
        --resourcesPath=dataflow-bucket
        
        # If not included at Spring Cloud Dataflow server
            --dataflow-server-uri=http://scdf-server.default.svc.cluster.local:80
        
        --inputFile=dataflow-bucket:sample-data.zip --resourcesPath=dataflow-bucket --dataflow-server-uri=http://scdf-server.default.svc.cluster.local:80

- Useful commandos:

        max-wait-time: 

![Parameters to configure tasks](images/parameters-to-composite-tasks.png)   

Links:

  - [Official Github repository](https://github.com/spring-cloud-task-app-starters/composed-task-runner/blob/master/spring-cloud-starter-task-composedtaskrunner/README.adoc)
  - [Reference Composed Task](https://docs.spring.io/spring-cloud-dataflow/docs/1.2.0.RELEASE/reference/html/spring-cloud-dataflow-composed-tasks.html)
  - [Docs Composited Tasks](http://docs.spring.io/spring-cloud-dataflow/docs/1.2.0.BUILD-SNAPSHOT/reference/htmlsingle/#spring-cloud-dataflow-composed-tasks)  
  
### S3

In order to create a S3 bucket, is necessary also to create an user and its policies. 

See `AWS.md` to know the process to create manually a bucket and user.

### PostgreSQL

In order to create the databases exsist some ways, depending on the environment and depending if the database already exist.

- Using **custom** database, it can be created multiple databases during the **creation time**. In the current example, and using *docker-compose* method, it can be created a script and added into a volume, within the `/docker-entrypoint-initdb.d` folder.
- Using external database, this must be done *manually*. Using initContainers or a command to create the database.


        docker run -d -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=password -p 5432:5432 postgres:10.7-alpine

        PGPASSWORD=password createuser -h localhost -p 5432 -U postgres dataflow
        PGPASSWORD=password createdb -h localhost -p 5432 -O dataflow -U postgres dataflow
        
In the case using kubernetes, the creation of the databases is performed by the migration tool, before the container is initialized. 
Since postgres does not allow the jdbc connection parameter `?createDatabaseIfNotExist=true`.

#### Private Database

In order to connect remotely to another database that is not public, it can be accessed via `port-forwarding` and through the bastion or any DMZ node.

    # If not added before
    ssh-add .ssh/bastion-key
    
    ssh -NL local-port:remote-private-host:remote-private-port user@remote-public-host 
    #  -L [bind_address:]port:host:hostport
    #          Specifies that the given port on the local (client) host is to be
    #          forwarded to the given host and port on the remote side.
    #  -N  Do not execute a remote command.  This is useful for just for‐
    #          warding ports (protocol version 2 only).
 
    ssh -NL 5432:eks-lab-dev-db.cwekrnapay4v.eu-west-2.rds.amazonaws.com:5432 ubuntu@35.176.168.219 -v
    
    # if using ssh agent (config)
    ssh -NL 5432:eks-lab-dev-db.cwekrnapay4v.eu-west-2.rds.amazonaws.com:5432 bastion
    
Finally, it can be used `localhost:5432` to access to the remote database and through the *port-forwarded*

> Connect to default database `postgres`

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
     
- To enter into a container (k8s) currently running, use the following command

        kubectl exec batchjobtask-394317zg6p -i -t -- sh     

 - To run into a docker container, that has not an entry point.

        # Redefine the --entrypoint if there is already one
        docker run -it --entrypoint=/bin/sh minio/mc 
        
- Pods are running forever in kubernetes.
    
    ```json
    spring:
      cloud:
        task:
          # Not available yet
          #closecontext:
          #  enabled: true
          closecontextEnabled: true
    ```

#### HELM

- Create and initialize the helm chart

        helm create scdf-batch

- Get the dependencies within the `requirements.yaml` file inside the helm chart.

        helm dependency update
        
> Add needed repo by using: `helm repo add incubator https://kubernetes-charts-incubator.storage.googleapis.com/` 

- Prepare the execution plan before installing the package

        helm install --debug --dry-run --name scdf-batch-lab --namespace dev-lab --set global.postgresql.enabled=true,postgresql.service.type=NodePort,minio.enabled=true,minio.service.type=NodePort,spring-cloud-data-flow.server.service.type=NodePort .
               
- Install the helm chart
          
        helm install --name scdf-batch-lab --namespace dev-lab --set global.postgresql.enabled=true,postgresql.service.type=NodePort,minio.enabled=true,minio.service.type=NodePort,spring-cloud-data-flow.server.service.type=NodePort .
                
        # List all the charts deployed
        helm list
        
    > Install the helm chat using custome parameters via `--set` or via `--values` parameters

- Check all deployments currently running.

        kubectl get pods,svc,secrets -n dev-lab

- Update current helm definition 

        helm upgrade scdf-batch-lab .
        
        helm upgrade scdf-batch-lab --set postgres.service.type=NodePort .
                
- Delete helm previously created

        helm del scdf-batch-lab --purge

#### References

- [Spring Cloud Dataflow releases version matrix](https://github.com/spring-cloud/spring-cloud-dataflow/releases)
- [Spring Cloud Dataflow kubernetes deployer releases](https://github.com/spring-cloud/spring-cloud-deployer-kubernetes/releases)
- [Spring App Starters Composed Task Runner releases](https://github.com/spring-cloud-task-app-starters/composed-task-runner/releases)
- [Spring Cloud Dataflow repository](http://repo.spring.io/milestone/org/springframework/cloud/)
- [Spring data flow with kubernetes](https://labnotes.panderalabs.com/spring-cloud-data-flow-and-docker-kubernetes-99a19f2dbab3)
- [Spring Cloud Deployer Kubernetes](https://github.com/spring-cloud/spring-cloud-deployer-kubernetes)
- [Routine Jobs with Kubernetes,](https://medium.com/pismolabs/routine-jobs-with-kubernetes-spring-cloud-dataflow-and-spring-cloud-task-d943bf107a8)
- [Spring Cloud Dataflow Shell](https://docs.spring.io/spring-cloud-dataflow/docs/current/reference/htmlsingle/#shell)
- http://what-when-how.com/Tutorial/topic-194n8n2/Spring-Batch-54.html
- https://github.com/spring-cloud-task-app-starters/timestamp-batch
- https://stackoverflow.com/questions/54627261/spring-cloud-task-app-composed-task-runner-doesnt-shutdown
- https://spring.io/blog/2019/03/06/spring-cloud-data-flow-and-skipper-2-0-ga-released
- https://github.com/shahbour/task-k8s/tree/master/task-request/src/main/java/com/shahbour