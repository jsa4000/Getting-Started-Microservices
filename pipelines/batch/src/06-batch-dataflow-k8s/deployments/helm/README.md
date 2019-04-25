# DataFlow Helm Chart

## Infrastructure

### Resources

- VPC 
  - 3 Availabilibity Zones for HA
  - Public Network: Bastion EC2 Only
  - Private Network: EKS cluster
  - Database Network (pricvate): RDS instance
  - NAT
  - 2 Routers (Public and Private networks)
- Route 53, with delate zone for the current VPC domain
- RDS Instance (PostgreSQL)
- EKS cluster
  - 2 On-demand Instances
  - 2 Sport instances

### IaC

Be sure to have enough **credentials** for AWS (admin) and S3 Bucket to store remote state `terraform-state-lab`

> **DynamoDB** is not used in this case for remote state

Following dependencies are needed:

- Terraform (0.11.13)
- Terragrunt (v0.18.2)
- kubectl (v1.14.0)
- awscli 
- aws-iam-authenticator

1. Add credentials for AWS using *environment variables*

    ```bash
    export AWS_ACCESS_KEY_ID="**********"
    export AWS_SECRET_ACCESS_KEY="**********"
    export AWS_DEFAULT_REGION="eu-west-2"
    ```
    
1. Deploy using terraform 

    ```bash
    cd /vagrant/files/create-eks-aws/deployment/eu-west-2/development
    terragrunt get terragrunt-source-update
    terragrunt plan
    terragrunt apply
    ```

    > Wait until the process ends... 

1. Copy the **outputs** in green in a secure place and encrypted

    - bastion_public_ip
    - eks_cluster_endpoint
    - eks_cluster_security_group_id
    - rds_instance_address
    - rds_instance_arn
    - rds_instance_endpoint
    - storage_bucket_name
    - storage_bucket_user

1. Initialize the **EKS** cluster

    ```bash
    terragrunt output eks_kubectl_config > eks_kubectl_config
    mkdir ~/.kube
    mv eks_kubectl_config ~/.kube/eks_kubectl_config
    export KUBECONFIG=$KUBECONFIG:~/.kube/eks_kubectl_config
    ```

1. Verify the EKS cluster

    ```bash
    kubectl get namespaces
    kubectl get services
    kubectl get nodes -o wide
    
    kubectl version
    kubectl cluster-info
    ```
 
 ## Kubernetes
 
 ### Dashboard
 
 1. Install the default dashboard ``
 
    kubectl apply -f https://raw.githubusercontent.com/kubernetes/dashboard/master/aio/deploy/recommended/kubernetes-dashboard.yaml
 
1. Got to the root folder at `/vagrant/files/create-eks-aws` 
 
1. Add RBAC permissions for the dashboard (**localhost** only)
 
    ```bash
    kubectl apply -f yaml/eks-admin-service-account.yaml
    kubectl apply -f yaml/eks-admin-cluster-role-binding.yaml
    kubectl -n kube-system describe secret $(kubectl -n kube-system get secret | grep eks-admin | awk '{print $1}')
    kubectl proxy 
    ```
    
 1. Remove authentication (use current host IP)

    ```bash
    kubectl apply -f yaml/dashboard-no-auth.yaml
    kubectl apply -f yaml/kubernetes-dashboard.yaml
    kubectl proxy --port=9999 --address='10.0.0.12' --accept-hosts="^*$"
    ````
         
 1. Access dashboard using following [Url](http://10.0.0.12:9999/api/v1/namespaces/kube-system/services/https:kubernetes-dashboard:/proxy/)
 
 
 ### Helm
 
Following dependencies are needed
 
 - Helm (*current v2.13.1*)
 
Install **Helm** (`tiller`) into kubernetes cluster
 
```bash
kubectl -n kube-system create sa tiller
kubectl create clusterrolebinding tiller --clusterrole cluster-admin --serviceaccount=kube-system:tiller
helm init --service-account tiller
```
 
> Check tiller service has been installed successfully `helm version`
 
Install `traefik` as default ingress controller
 
    helm install --name traefik-ingress --namespace kube-system --set dashboard.enabled=true,metrics.prometheus.enabled=true,rbac.enabled=true stable/traefik
    
Install `Prometheus` as `Grafana` (`EFK` is optional)
 
    helm install --name prometheus --namespace prometheus --set server.ingress.enabled=true,server.ingress.annotations."kubernetes\.io/ingress\.class"=traefik,server.ingress.hosts={prometheus.eks-lab.com},alertmanager.ingress.enabled=true,alertmanager.ingress.annotations."kubernetes\.io/ingress\.class"=traefik,alertmanager.ingress.hosts={alertmanager.eks-lab.com} stable/prometheus
    helm install --name grafana-dashboard --namespace grafana --set persistence.enabled=true,ingress.enabled=true,ingress.annotations."kubernetes\.io/ingress\.class"=traefik,ingress.hosts={grafana.eks-lab.com} stable/grafana
    
Get Load Balander IP (ExternalIP) and update `hosts` file for dns configured

> Waiut until the ELB is created. Get one IP address used for the static IP assigned to the LoadBalancer and added to the /etc/hosts

```bash
host $(kubectl describe svc traefik-ingress --namespace kube-system | grep Ingress | awk '{print $3}') |  awk '{print $NF}'
code /etc/hosts
```

```bash
35.177.119.106 grafana.eks-lab.com
35.177.119.106 prometheus.eks-lab.com
35.177.119.106 alertmanager.eks-lab.com
35.177.119.106 elasticsearch.eks-lab.com
35.177.119.106 kibana.eks-lab.com
35.177.119.106 dataflow.eks-lab.com
```

```bash
brew install gnu-sed
sudo gsed -i 's/${PREVIOUS_IP}/${CURRENT_IP}/g' /etc/hosts
```    

Get password for Grafana dashboard (user `admin` )
 
    kubectl get secret --namespace grafana grafana-dashboard -o jsonpath="{.data.admin-password}" | base64 --decode ; echo
    
Use `http://prometheus-server.prometheus.svc.cluster.local` as URL in grafana **datasource** and Install following dashboards
 
   - Node eXporter: `405`
   - Cadvisor: `893`
   - Kubernetes cluster monitoring: `1621`
   
## Spring Cloud Dataflow

### Installation

- Deploy SGDF using current helm chart.

  > It can be used also --set to set additional properties in addition to the values file.

    helm install --debug --dry-run --name scdf-batch-lab --namespace dev-lab -f values-aws.yaml .
    
    helm install --name scdf-batch-lab --namespace dev-lab -f values-aws.yaml .
    
- Upgrade a helm chart verion

    helm upgrade scdf-batch-lab -f values-aws.yaml --set spring-cloud-data-flow.server.version=2.0.2.RELEASE .
    
> Previous configuration file is needed to get the proper state.

 - Remove the helm chart
 
        helm delete scdf-batch-lab --purge    
    
- Verify chart installation

        kubectl get pods -n dev-lab
    
    ```bash
    NAME                                                         READY   STATUS      RESTARTS   AGE
    scdf-batch-lab-batch-process-migration-job-v6565             0/1     Completed   0          1m
    scdf-batch-lab-batch-process-rest-service-6ff8766645-x62qh   1/1     Running     0          1m
    scdf-batch-lab-data-flow-server-5d4777dfc4-p9k66             1/1     Running     0          1m
    ```
    
- Verify [SCDF dashboard](http://dataflow.eks-lab.com/dashboard) can be accessed remotely from ingress.

- Create a tunnel to access to database locally using `bastion`

> It is necessary the private-key `~/.ssh/bastion-key` generated prior the creation of the bastion.

  - Edit your local `~/.ssh/config` file and add the bastion configuration (Public DNS):
   
     ```bash
      Host bastion
        Hostname 3.8.123.40
        User ubuntu
        ForwardAgent yes
      ```
  - Check it can be accessed to bastion

    ```
    ssh-add -K ~/.ssh/bastion-key
    ssh bastion
    ```

- Finally create the port-forwarding through the Bastion to the RDS instance.

        ssh -NL 5432:eks-lab-dev-db.cwekrnapay4v.eu-west-2.rds.amazonaws.com:5432 bastion
        
- Access to data base using the following `jdbc` url.

        jdbc:postgresql://localhost:5432/postgres   
 
 ### Batch Process
 
 - Add following file `/deployments/compose/files/sample-data-prod.zip` into bucket: `eks-lab-dev-buckett`
 
 - Connect to dataflow server via shell or dashboard
 
     ```bash
     kubectl get pods -n dev-lab | grep 'scdf-batch-lab-data-flow-server-' | awk '{print $1}'
     kubectl exec -n dev-lab scdf-batch-lab-data-flow-server-64488b697-qh4z9 -it -- java -jar shell.jar --dataflow.uri=http://localhost:8080
     ```
 
 - Create a new application, using the generated docker image
 
     ```bash
     # Resgister apps
     app register --type task --name batch-uploader-app --uri docker:jsa4000/dataflow-batch-uploader-k8s:0.0.1-SNAPSHOT
     app register --type task --name batch-process-prod-app --uri docker:jsa4000/dataflow-batch-process-prod-k8s:0.0.1-SNAPSHOT
     
     app list
     
     # Create task with previous app
     task create batch-uploader-task --definition "batch-uploader-app --version=0.0.1"
     task create batch-process-prod-task --definition "batch-process-prod-app --version=0.0.1"
       
     # Launch task individually
     task launch batch-uploader-task --arguments "--spring.profiles.active=k8s,master"

     # Launch using AWS (S3 + RDS)
     task launch batch-process-prod-task --arguments "--spring.profiles.active=k8s,master --inputFile=eks-lab-dev-bucket:sample-data-prod.zip --resourcesPath=eks-lab-dev-bucket/sample-data-prod --batch.departmentsUri=http://scdf-batch-lab-batch-process-rest-service:8080/departments --batch.storage.region=eu-west-2 --batch.storage.url=s3.amazonaws.com --batch.storage.accessKey=AKIAV2GSISOCPRVR7F5Q --batch.storage.secretKey=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 --batch.datasource.username=postgres --batch.datasource.url=jdbc:postgresql://eks-lab-dev-db.cwekrnapay4v.eu-west-2.rds.amazonaws.com:5432/db --batch.datasource.driverClassName=org.postgresql.Driver --batch.datasource.password=password"
     
     ```

- Check the results of the Jobs using the shell
     
     ``` 
     job execution list
     job execution display --id 1
     
     ```
     
 - Use the following parameters to debug launch the task, if  any error 
 
         kubectl get pods -n dev-lab
         kubectl logs -n dev-lab uploaderjobtask-3kwynk3v58
         
- To clean completed Jobs use the following command

    kubectl get pods -n dev-lab | awk '$2 ~ 0/1' | awk '{print $1}' | xargs kubectl delete pod -n dev-lab
         
### Benchmarks

In order to perform benchmarks, a big file must be generated and different configurations are tested to get the time the job takes to complete.

- Launch the task previously defined and with the commands (`00:33:19.672` and `454176` writes and `1000000` read)

        # Create and upload the data to test 'sample-data-prod.zip'
        task launch batch-process-prod-task --arguments "--batch.max-workers=1 --spring.profiles.active=k8s,master --inputFile=eks-lab-dev-bucket:sample-data-prod.zip --resourcesPath=eks-lab-dev-bucket/sample-data-prod --batch.departmentsUri=http://scdf-batch-lab-batch-process-rest-service:8080/departments --batch.storage.region=eu-west-2 --batch.storage.url=s3.amazonaws.com --batch.storage.accessKey=AKIAV2GSISOCPRVR7F5Q --batch.storage.secretKey=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 --batch.datasource.username=postgres --batch.datasource.url=jdbc:postgresql://eks-lab-dev-db.cwekrnapay4v.eu-west-2.rds.amazonaws.com:5432/db --batch.datasource.driverClassName=org.postgresql.Driver --batch.datasource.password=password"
 
- Launch the task previously defined and with the commands (`00:09:39.743` and `454176` writes and `1000000` read)

        # Create and upload the data to test 'sample-data-prod.zip' 
        task launch batch-process-prod-task --arguments "--batch.max-workers=8 --spring.profiles.active=k8s,master --inputFile=eks-lab-dev-bucket:sample-data-prod.zip --resourcesPath=eks-lab-dev-bucket/sample-data-prod --batch.departmentsUri=http://scdf-batch-lab-batch-process-rest-service:8080/departments --batch.storage.region=eu-west-2 --batch.storage.url=s3.amazonaws.com --batch.storage.accessKey=AKIAV2GSISOCPRVR7F5Q --batch.storage.secretKey=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 --batch.datasource.username=postgres --batch.datasource.url=jdbc:postgresql://eks-lab-dev-db.cwekrnapay4v.eu-west-2.rds.amazonaws.com:5432/db --batch.datasource.driverClassName=org.postgresql.Driver --batch.datasource.password=password"
        
- Launch the task previously defined and with the commands (`00:07:09.487` and `454176` writes and `1000000` read)

        # Create and upload the data to test 'sample-data-prod.zip' 
        task launch batch-process-prod-task --arguments "--batch.max-workers=10 --spring.profiles.active=k8s,master --inputFile=eks-lab-dev-bucket:sample-data-prod.zip --resourcesPath=eks-lab-dev-bucket/sample-data-prod --batch.departmentsUri=http://scdf-batch-lab-batch-process-rest-service:8080/departments --batch.storage.region=eu-west-2 --batch.storage.url=s3.amazonaws.com --batch.storage.accessKey=AKIAV2GSISOCPRVR7F5Q --batch.storage.secretKey=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 --batch.datasource.username=postgres --batch.datasource.url=jdbc:postgresql://eks-lab-dev-db.cwekrnapay4v.eu-west-2.rds.amazonaws.com:5432/db --batch.datasource.driverClassName=org.postgresql.Driver --batch.datasource.password=password"
                
- Useful queries in SQL

```sql
-- Count all the records 
SELECT count(*) FROM customer;

-- DELETE ALL RECORDS
DELETE FROM customer;  
````
        
### Scheduled Tasks

- Create a new task

    > This is a work-around since there is an [issue](https://github.com/spring-cloud/spring-cloud-dataflow/issues/3187) using multiple profiles within arguments)
    
    ```bash
    # Resgister app (if not exists)
     app register --type task --name batch-uploader-app --uri docker:jsa4000/dataflow-batch-uploader-k8s:0.0.1-SNAPSHOT
     
     # Create task with previous app
     task create batch-uploader-task-schedule --definition "batch-uploader-app --version=0.0.1 --spring.profiles.active=k8s,master"
    ```

- Open **SCDF dashboard** and enter into the `tasks` pannel.

- Click onto the *down-arrow* on previous task created and select `Schedule Task`.

> Use the following *cron* expression to lauch a task per minute: `*/1 * * * *`. It uses the standard from K8s https://kubernetes.io/docs/tasks/job/automated-tasks-with-cron-jobs/

Since it is using Kubernete's **cronjob**, it does not work fine with job names using specific charaters. 

> Recommended using **lower case and only-letters** for the schedule name. i.e `myschedule`

```bash
kubectl get cronjob --all-namespaces
NAMESPACE   NAME         SCHEDULE      SUSPEND   ACTIVE   LAST SCHEDULE   AGE
dev-lab     myschedule   */1 * * * *   False     0        34s             3m
```

## Composite Task

It is possible to lauch multiple task using composition.

Create the mandatory `composed-task-runner ` app and the apps that are going to be used within the tasks created.

 ```bash
# Resgister apps
app register --type task --name composed-task-runner --uri docker:springcloudtask/composedtaskrunner-task:2.1.0.RELEASE
app register --type task --name timestamp --uri docker:springcloudtask/timestamp-task:2.0.0.RELEASE --metadata-uri maven://org.springframework.cloud.task.app:timestamp-task:jar:metadata:2.0.0.RELEASE
app register --type task --name launcher-app --uri docker:jsa4000/dataflow-task-launcher:0.0.1-SNAPSHOT
app register --type task --name notifier-app --uri docker:jsa4000/dataflow-task-notifier:0.0.1-SNAPSHOT

app list

# Create single task with previous apps
task create launcher-task --definition "launcher-app --verion=0.1.0"
task create notifier-task --definition "notifier-app --verion=0.1.0"

## Original created within Spring Cloud data-flow server dashboard
# "launcher-root: launcher-app 'COMPLETED'->launcher-complete: launcher-app 'FAILED'->launcher-fail: launcher-app"
  
# Create composed tasks
task create my-composed-task --definition "<aaa: timestamp || bbb: timestamp>"
task create launcher-composite-task --definition "launcher-root: launcher-app 'COMPLETED'->launcher-complete: launcher-app --result=COMPLETED 'FAILED'->launcher-fail: launcher-app --result=FAILED"  
  
```  

Check the tasks created using composite task. Each repeated task is created again with an unique name.

```bash
task list

╔═════════════════════════════════════════╤══════════════════════════════════════════════════════════════════════════════════════════════════════════════╤═══════════╗
║                Task Name                │                                               Task Definition                                                │Task Status║
╠═════════════════════════════════════════╪══════════════════════════════════════════════════════════════════════════════════════════════════════════════╪═══════════╣
║launcher-task                            │launcher-app --verion=0.1.0                                                                                   │COMPLETE   ║
║launcher-composite-task-launcher-root    │launcher-app                                                                                                  │UNKNOWN    ║
║launcher-composite-task-launcher-complete│launcher-app --result=COMPLETED                                                                               │UNKNOWN    ║
║launcher-composite-task-launcher-fail    │launcher-app --result=FAILED                                                                                  │UNKNOWN    ║
║launcher-composite-task                  │launcher-root: launcher-app 'COMPLETED'->launcher-complete: launcher-app 'FAILED'->launcher-fail: launcher-app│UNKNOWN    ║
╚═════════════════════════════════════════╧══════════════════════════════════════════════════════════════════════════════════════════════════════════════╧═══════════╝
```

Launch the composite task created previously `launcher-composite-task`. 

```bash  
# Launch task individually
task launch launcher-task --arguments "--spring.profiles.active=k8s"
task launch notifier-task --arguments "--mail.auth.username= --mail.auth.password="

# If not configured withon the SCDF server, 
# It must be **specified** the URL where data-flow server is located. "--dataflow-server-uri=http://scdf-server.default.svc.cluster.local:80"

task launch my-composed-task --arguments "--increment-instance-enabled=true --max-wait-time=50000 --split-thread-core-pool-size=4" --properties "app.my-composed-task.bbb.timestamp.format=dd/MM/yyyy HH:mm:ss"
task launch launcher-composite-task --arguments "--increment-instance-enabled=true" --properties "app.launcher-composite-task.launcher-root.spring.profiles.active=k8s,app.launcher-composite-task.launcher-complete.spring.profiles.active=k8s"

# Next composite task will throw an error if the `batch_job_execution_params` table has not been modified
task launch launcher-composite-task --arguments "--increment-instance-enabled=true" --properties "app.launcher-composite-task.launcher-root.spring.profiles.active=k8s,app.launcher-composite-task.launcher-complete.spring.profiles.active=k8s,app.launcher-composite-task.launcher-fail.spring.profiles.active=k8s"

# Get the result
task execution list
job execution list
job execution display --id 1
```