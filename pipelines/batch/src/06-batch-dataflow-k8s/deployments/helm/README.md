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

> Get one IP address used for the static IP assigned to the LoadBalancer and added to the /etc/hosts

```bash
host $(kubectl describe svc traefik-ingress --namespace kube-system | grep Ingress | awk '{print $3}') |  awk '{print $NF}'
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
 
   - Node eXporter: `405405`
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
     task launch batch-process-prod-task --arguments "--spring.profiles.active=k8s,master --inputFile=eks-lab-dev-bucket:sample-data-prod.zip --resourcesPath=eks-lab-dev-bucket/sample-data-prod --batch.departmentsUri=http://scdf-batch-lab-batch-process-rest-service:8080/departments --batch.storage.region=eu-west-2 --batch.storage.url=s3.amazonaws.com --batch.storage.accessKey=AKIAV2GSISOCDOVXNQ3I --batch.storage.secretKey=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 --batch.datasource.username=postgres --batch.datasource.url=jdbc:postgresql://eks-lab-dev-db.cwekrnapay4v.eu-west-2.rds.amazonaws.com:5432/db --batch.datasource.driverClassName=org.postgresql.Driver --batch.datasource.password=password"
     
     ```

- Check the results of the Jobs using the shell
     
     ``` 
     job execution list
     job execution display --id 1
     
     ```
     
 - Use the following parameters to debug launch the task, if  any error 
 
         kubectl get pods -n dev-lab
         kubectl logs -n dev-lab uploaderjobtask-3kwynk3v58