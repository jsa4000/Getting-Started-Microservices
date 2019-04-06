# Partitioned Batch Job in Kubernetes

This project has been forked from the [spring-cloud-task](https://github.com/spring-cloud/spring-cloud-task) official repository.

This example can be located at `/spring-cloud-task-samples/partitioned-batch-job`


## How to run

- Build the project and generate the docker image (use `gradle jibDockerBuild` and `gradle jib`)

- Open a shell to register the app adn task

        kubectl get pods
        
        kubectl exec scdf-server-58cb976466-9gqrv -it -- java -jar shell.jar --dataflow.uri=http://localhost:80
        
- Register the app and create the task

     app register --type task --name partitioned-batch-job-app --uri docker:jsa4000/dataflow-partitioned-batch-job:0.0.1-SNAPSHOT
     task create partitioned-batch-job-task --definition "partitioned-batch-job-app"
     
- Finally launch the task     
   
     task launch partitioned-batch-job-task --arguments "--spring.profiles.active=k8s,master"
