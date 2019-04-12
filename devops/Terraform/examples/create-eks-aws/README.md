# Create AWS Infra

## Identity And Access Management (IAM)

This is the administration console of AWS that manage all related with Users, Roles, Security, Policies, Groups, etc..

In order to be able to allow **terraform** to perform changes in AWS, it is needed to create an user with **Programatic Access** (Enables an access key ID and secret access key for the AWS API, CLI, SDK, and other development tools.)

If using `AWSCLI`, following commands will create an user that can be used with terraform. However an User must exist already in order to execute these commands.

> Usually when an aws account is created, normally the `access-key-id` and the `secret-key-id` are also provided with the user.

- Create IAM terraform User

        aws iam create-user --user-name terraform

- Add to newly created terraform user IAM admin policy

    > NOTE: For production or event proper testing account you may need tighten up and restrict access for terraform IAM user

        aws iam attach-user-policy --user-name terraform --policy-arn arn:aws:iam::aws:policy/AdministratorAccess

- Create access keys for the user

    > NOTE: This Access Key and Secret Access Key will be used by terraform to manage infrastructure deployment

        aws iam create-access-key --user-name terraform

- Delete user

        aws iam detach-user-policy --user-name terraform --policy-arn arn:aws:iam::aws:policy/AdministratorAccess

        aws iam list-access-keys --user-name terraform  --query 'AccessKeyMetadata[*].{ID:AccessKeyId}' --output text

        aws iam delete-access-key --user-name terraform --access-key-id OUT_KEY

        aws iam delete-user --user-name terraform

## Installation

- Terrafrom (0.11.13)

        sudo apt-get install unzip
        wget https://releases.hashicorp.com/terraform/0.11.13/terraform_0.11.13_linux_amd64.zip
        unzip terraform_0.11.13_linux_amd64.zip
        sudo mv terraform /usr/bin

- Terragrunt (v0.18.2)

        cd /tmp
        wget https://github.com/gruntwork-io/terragrunt/releases/download/v0.18.2/terragrunt_linux_amd64
        sudo mv terragrunt_linux_amd64 /usr/bin/terragrunt
        sudo chmod a+wrx /usr/bin/terragrunt

- Helm (*current v2.13.1*)

        curl https://raw.githubusercontent.com/kubernetes/helm/master/scripts/get | bash

- Install `awscli`

        sudo apt-get install awscli

- Install `aws-iam-authenticator`

    > [Official documentAation](https://docs.aws.amazon.com/eks/latest/userguide/install-aws-iam-authenticator.html)

        wget https://amazon-eks.s3-us-west-2.amazonaws.com/1.11.5/2018-12-06/bin/linux/amd64/aws-iam-authenticator
        sudo mv aws-iam-authenticator /usr/bin/aws-iam-authenticator
        sudo chmod a+wrx /usr/bin/aws-iam-authenticator

- **Validate** the installation
  
        terraform version
        terragrunt --version
        awscli --version
        aws-iam-authenticator version
        helm version

## Creating key-pair for SSH

- Create Key Pair

        ssh-keygen -t rsa -b 4096 -C "bastion@gmail.com" -f ~/.ssh/bastion-key

> **Keep** both keys in a secure place!

- Add Key Pair to Terraform setup

        cat ~/.ssh/bastion-key.pub | pbcopy

  *terraform.tfvars*

  ```tf
  key_name   = "your_key_name"
  public_key = "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAACAQDWbz6ur89BKQ+am87EovJsv6g9QpbOiw13lTF7Kw1StbQAmkcGGrNTK2LIWsP3cQf+P+gptRAJbeeB1jQKZ283TwwREIv  +l5AMKrbEkanOF4zsc8a9zitejlOLvVUxtVoMi5ROVYD2dLKjqAbDtqIC9LmMD+hcpqcXLhS6t+HVSVI862dTNVFY1EGukLGQ3IEJfw5v7FDzLn72NsuUiXEeCZu8DtlXLCTYRnqv  +XkJQWVocPdFDUWISSIQ0CTFu+GJvJjdqDyAhYo3it7Eybj6XuSgLDwkQcNU45Eee4Nn7LwV+f4Av8D25m4FZOfpWaj5+q9Fc9nRdIsB7P0oFgj5YoaTngQKy27MJ5UppMO7OOhriurJ/  PBOrGpeqPcftWKLpcHLIGrm3ndoDKQx12R1s0gyYpA4JuNUWHYcxNrFa2rs/6AoFuS7wNUmM+DYB8iTjOl6dT8dS5AgMxGoZ3NepMPYilw1gf  +gw9Ft3pHs2IMfDfqwZpXga8KdYwxBmRakpHdA7Nzje8ufvP/TBawsqVcW7z5gG9uPhYtfnYYezSIxv56PMSWEfqchkz  +raPsElzIGtPcC1snncQlau95utV25r88BzXhCMJwNy9aDNEfSrm5SORlA97xicroCOuRjw2PnQyIXKvWDZtyqX5799x37K/HDYpJnvcgwpTlDZQ== your_email@example.com"
  ```

- Edit your local `~/.ssh/config` file and add the following:

  ```bash
  Host bastion
    Hostname ec2-35-178-181-167.eu-west-2.compute.amazonaws.com
    User ubuntu
    ForwardAgent yes
  ```

- Finally we can `ssh` onto the **bastion**:

        ssh -i ~/.ssh/bastion-key ubuntu@ec2-35-178-181-167.eu-west-2.compute.amazonaws.com

        # If created `~/.ssh/config` file
        ssh -i ~/.ssh/your_key_name bastion

- Or using `ssh-agent`

        ssh-add -K ~/.ssh/bastion-key
        ssh bastion

- Once you are inside the bastion, you can connect to other networks (eks nodes)

        ssh ec2-user@ip-10-10-3-169.eu-west-2.compute.internal

## Terraform

- Add your aws credentials to the environment variables.

        ```bash
        export AWS_ACCESS_KEY_ID=..
        export AWS_SECRET_ACCESS_KEY=..
        export AWS_DEFAULT_REGION=eu-west-1
        ```

- Once we have terraform IAM account created we can proceed to next step creating dedicated `S3 bucket` to keep terraform state files (`.tfstate`)

        aws s3 mb s3://terraform-state-lab --region eu-west-1

- Enable versioning on the newly created bucket

        aws s3api put-bucket-versioning --bucket terraform-state-lab --versioning-configuration Status=Enabled

- Check the bucket has been created successfully

        aws s3api list-buckets --query "Buckets[].Name"

- Delete bucket

        aws s3 rm s3://terraform-state-lab --recursive

        aws s3api put-bucket-versioning --bucket terraform-state-lab --versioning-configuration Status=Suspended

        aws s3api delete-objects --bucket terraform-state-lab --delete \
        "$(aws s3api list-object-versions --bucket terraform-state-lab | \
        jq '{Objects: [.Versions[] | {Key:.Key, VersionId : .VersionId}], Quiet: false}')"

        aws s3 rb s3://terraform-state-lab --force

## Terragrunt

- **Initialize** terragrunt. (Delete cache and download dependencies, modules and sources)
  
        terragrunt get --terragrunt-source-update

- Prepare a **Plan** with the resources to be `created/modified/deleted`
  
         terragrunt plan

- **Apply** the changes to be made

        terragrunt apply

- **Destroy** infrastructure

    > Use `-auto-approve` to forze the deletion without prompt

        terragrunt destroy

> Use `TF_WARN_OUTPUT_ERRORS=1 terragrunt destroy`, if find it any output or warning after the execution.

- It can be run specific modules only (i.e `module.eks`).

        terragrunt destroy -target=module.eks

## Kubernetes

This process should be done manually if the previous process using terragrunt, it was a problem at the end trying to bootstrap the eks  cluster.

1. Create new AWS CLI profile

    In order to use `kubectl` with EKS we need to set new AWS CLI profile

    > NOTE: will need to use secret and access keys from `terraform.tfvars`or using environment variables instead.

        cat terraform.tfvars
        aws configure --profile terraform
        export AWS_PROFILE=terraform

2. Configure `kubectl` to allow us to connect to EKS cluster

    > In terraform configuration we **output** configuration file for kubectl

        terragrunt output eks_kubectl_config > eks_kubectl_config

3. Add output to `~/.kube/`

        mkdir ~/.kube
        mv eks_kubectl_config ~/.kube/eks_kubectl_config

        # Set the context with the kube-config to use with kubectl
        # Note AWS crentials must be provided
        export KUBECONFIG=$KUBECONFIG:~/.kube/eks_kubectl_config

4. Verify kubectl connectivity

        kubectl get namespaces
        kubectl get services

        # If 'No resources found.' go to for the next step 5 (manual bootstrap)
        kubectl get nodes

5. [Manual Bootstrap] Second part we need to allow EKS to add nodes by running `configmap`

        terragrunt output eks_config_map_aws_auth > config_map_aws_auth.yaml
        kubectl apply -f config_map_aws_auth.yaml

6. Now you should be able to see nodes

        kubectl get nodes -o wide

        NAME                                        STATUS   ROLES    AGE   VERSION   INTERNAL-IP   EXTERNAL-IP   OS-IMAGE         KERNEL-VERSION                CONTAINER-RUNTIME
        ip-10-10-0-243.eu-west-1.compute.internal   Ready    <none>   3m    v1.11.9   10.10.0.243   <none>        Amazon Linux 2   4.14.104-95.84.amzn2.x86_64   docker://18.6.1
        ip-10-10-1-191.eu-west-1.compute.internal   Ready    <none>   3m    v1.11.9   10.10.1.191   <none>        Amazon Linux 2   4.14.104-95.84.amzn2.x86_64   docker://18.6.1
        ip-10-10-1-52.eu-west-1.compute.internal    Ready    <none>   3m    v1.11.9   10.10.1.52    <none>        Amazon Linux 2   4.14.104-95.84.amzn2.x86_64   docker://18.6.1
        ip-10-10-2-227.eu-west-1.compute.internal   Ready    <none>   3m    v1.11.9   10.10.2.227   <none>        Amazon Linux 2   4.14.104-95.84.amzn2.x86_64   docker://18.6.1

7. Cluster Info and kubectl version (client and server)

        kubectl version

        kubectl cluster-info

        Kubernetes master is running at https://E12965DE82F9F8E204ED4196D9260329.sk1.eu-west-1.eks.amazonaws.com
        CoreDNS is running at https://E12965DE82F9F8E204ED4196D9260329.sk1.eu-west-1.eks.amazonaws.com/api/v1/namespaces/kube-system/services/kube-dns:dns/proxy

        To further debug and diagnose cluster problems, use 'kubectl cluster-info dump'.

### Tools

#### Run Docker

It can be run docker containers interactively rby using the following command

        # Create container to run shell
        kubectl run --generator=run-pod/v1 my-shell --rm -i --tty --image ubuntu -- bash

        # Create container with postgresql
        kubectl run --generator=run-pod/v1 pg-shell --rm -i --tty --image=postgres -- sh
        # Connect to RDS postgre database
        psql postgresql://root:password@eks-lab-dev-db.ctlhqjtqc4hl.eu-west-1.rds.amazonaws.com:5432/postgres
        # User following command to list all the databases
        \l

#### Kubernetes Dashboard

- Deploy the Kubernetes Dashboard (*localhost*)

        kubectl apply -f https://raw.githubusercontent.com/kubernetes/dashboard/master/aio/deploy/recommended/kubernetes-dashboard.yaml

- Create an `eks-admin` Service Account and Cluster Role Binding

        kubectl apply -f yaml/eks-admin-service-account.yaml
        kubectl apply -f yaml/eks-admin-cluster-role-binding.yaml

- Connect to the Dashboard

        kubectl -n kube-system describe secret $(kubectl -n kube-system get secret | grep eks-admin | awk '{print $1}')

        kubectl proxy

- Deploy the Kubernetes Dashboard (*from another hosts*)

  > [Dashboard disabled by default for non localhost](https://stackoverflow.com/questions/51907168/not-able-to-login-to-kubernetes-dashboard-using-token-with-service-account)

        kubectl apply -f yaml/dashboard-no-auth.yaml
        kubectl apply -f yaml/kubernetes-dashboard.yaml
        kubectl proxy --port=9999 --address='10.0.0.12' --accept-hosts="^*$"

- Access to the dashboard (press *skip* button)

        https://10.0.0.12:9999/api/v1/namespaces/kube-system/services/https:kubernetes-dashboard:/proxy/

#### Helm

Helm is a tool for managing Kubernetes charts. Charts are packages of pre-configured Kubernetes resources.

- Find and use popular software packaged as Helm charts to run in Kubernetes
- Share your own applications as Helm charts
- Create reproducible builds of your Kubernetes applications
- Intelligently manage your Kubernetes manifest files
- Manage releases of Helm packages

##### Helm Installation

- Initialize `helm` to deploy the necessary pods into eks cluster

        kubectl -n kube-system create sa tiller

        kubectl create clusterrolebinding tiller --clusterrole cluster-admin --serviceaccount=kube-system:tiller

        helm init --service-account tiller

- Verify helm is correctly installed

        helm version

- To **remove** `tiller` from server

        helm reset --force

#### Traefik Ingress

**Traefik** is a modern HTTP reverse proxy and load balancer that makes deploying microservices easy. Traefik integrates with your existing infrastructure components (Docker, Swarm mode, Kubernetes, Marathon, Consul, Etcd, Rancher, Amazon ECS, ...) and configures itself automatically and dynamically. Pointing Traefik at your orchestrator should be the only configuration step you need.

- Install `traefik` for ingress controller using helm

        helm install --name traefik-ingress --namespace kube-system --set dashboard.enabled=true,metrics.prometheus.enabled=true,rbac.enabled=true stable/traefik

  ```bash
  NOTES:

  1. Get Traefik's load balancer IP/hostname:

  NOTE: It may take a few minutes for this to become available.

  You can watch the status by running:

          $ kubectl get svc traefik-ingress --namespace kube-system -w

  Once 'EXTERNAL-IP' is no longer '<pending>':

          $ kubectl describe svc traefik-ingress --namespace kube-system | grep Ingress | awk '{print $3}'

  1. Configure DNS records corresponding to Kubernetes ingres  resources to point to the load balancer IP/hostname found in step 1
  ```

- Check the configuration for `traefik-ingress`

      kubectl describe svc traefik-ingress --namespace kube-system
      kubectl get pod,svc --all-namespaces

#### Nginx Ingress

- Install `nginx-ingress` for ingress controller using helm

        helm install stable/nginx-ingress --name nginx-ingress --set controller.stats.enabled=true,controller.metrics.enabled=true

  ```bash
  NOTES:
  The nginx-ingress controller has been installed.
  It may take a few minutes for the LoadBalancer IP to be available.
  You can watch the status by running 'kubectl --namespace default get services   -o wide -w nginx-ingress-controller'
  
  An example Ingress that makes use of the controller:
  
    apiVersion: extensions/v1beta1
    kind: Ingress
    metadata:
      annotations:
        kubernetes.io/ingress.class: nginx
      name: example
      namespace: foo
    spec:
      rules:
        - host: www.example.com
          http:
            paths:
              - backend:
                  serviceName: exampleService
                  servicePort: 80
                path: /
      # This section is only required if TLS is to be enabled for the Ingress
      tls:
          - hosts:
              - www.example.com
            secretName: example-tls
  
  If TLS is enabled for the Ingress, a Secret containing the certificate and   key must also be provided:
  
    apiVersion: v1
    kind: Secret
    metadata:
      name: example-tls
      namespace: foo
    data:
      tls.crt: <base64 encoded cert>
      tls.key: <base64 encoded key>
    type: kubernetes.io/tls
  ```

- Check the configuration for `nginx-ingress`

        kubectl --namespace default get services -o wide -w nginx-ingress-controller

#### Prometheus and Grafana

- Install `Prometheus`

        helm install --name prometheus --namespace prometheus --set server.ingress.enabled=true,server.ingress.annotations."kubernetes\.io/ingress\.class"=traefik,server.ingress.hosts={prometheus.eks-lab.com},alertmanager.ingress.enabled=true,alertmanager.ingress.annotations."kubernetes\.io/ingress\.class"=traefik,alertmanager.ingress.hosts={alertmanager.eks-lab.com} stable/prometheus

    > The Prometheus server can be accessed via port 80 on the following DNS name from within your cluster: `prometheus-server.prometheus.svc.cluster.local`

- Install `Grafana`

        helm install --name grafana-dashboard --namespace grafana --set persistence.enabled=true,ingress.enabled=true,ingress.annotations."kubernetes\.io/ingress\.class"=traefik,ingress.hosts={grafana.eks-lab.com} stable/grafana

    > Get the credentials to access grafana dashboard

        kubectl get secret --namespace grafana grafana-dashboard -o jsonpath="{.data.admin-password}" | base64 --decode ; echo

- Set `prometheus` datasource and the dashboards into grafana (http://grafana.eks-lab.com/)

  > Use `http://prometheus-server.prometheus.svc.cluster.local` as URL in grafana datasource
  
  - Node eXporter: 405
  - Cadvisor: 893
  - Kubernetes cluster monitoring: 1621

- **Uninstall** helm packages

        helm del grafana-dashboard --purge
        helm del prometheus --purge

#### Install EFK

In this section it will be explained howto deploy EFK stack into kubernetes cluster.

##### Elasticsearch

[Stable helm Chart](https://hub.kubeapps.com/charts/stable/elasticsearch)

Install **Elastic Search** cluster, with only 1 replica and 2 clients.

    helm install --name elasticsearch --namespace logging --set client.ingress.enabled=true,client.ingress.annotations."kubernetes\.io/ingress\.class"=traefik,client.ingress.hosts={elasticsearch.eks-lab.com},client.replicas=1,master.replicas=2,data.replicas=1,cluster.env.MINIMUM_MASTER_NODES=2 stable/elasticsearch

> The official [troubleshooting](https://github.com/bitnami/kube-prod-runtime/blob/master/docs/troubleshooting.md#troubleshooting-elasticsearch
) explicitly says to increase the number of `max file descriptors` in EKS.

```bash
sudo sed -i '/"nofile": {/,/}/d' /etc/docker/daemon.json
sudo sed -i '/OPTIONS/d' /etc/sysconfig/docker
sudo systemctl restart docker
```

> To get the number of descriptors use the following command inside a worker node:

        docker run -it --rm ubuntu bash -c "ulimit -n -H"

> Or using kubectl from outside

        kubectl run --generator=run-pod/v1 my-shell --rm -i --tty --image ubuntu -- bash -c "ulimit -n -H"

Output, after installing the chart.

```bash
NOTES:
The elasticsearch cluster has been installed.

Elasticsearch can be accessed:

  * Within your cluster, at the following DNS name at port 9200:

    elasticsearch-client.logging.svc

  * From outside the cluster, run these commands in the same shell:

    export POD_NAME=$(kubectl get pods --namespace logging -l "app=elasticsearch,component=client,release=elasticsearch" -o jsonpath="{.items[0].metadata.name}")
    echo "Visit http://127.0.0.1:9200 to use Elasticsearch"
    kubectl port-forward --namespace logging $POD_NAME 9200:9200
```

Verify the current client can be accessed from ingress http://elasticsearch.logging.com:30537/ and http://elasticsearch.logging.com:30537/_count?pretty

##### Kibana

[Stable helm Chart](https://hub.kubeapps.com/charts/stable/kibana)

Install **kibana** helm chart

        helm install --name kibana --namespace logging --set ingress.enabled=true,ingress.annotations."kubernetes\.io/ingress\.class"=traefik,ingress.hosts={kibana.eks-lab.com},env.ELASTICSEARCH_URL=http://elasticsearch-client:9200 stable/kibana

        helm install --name kibana --namespace logging --set ingress.enabled=true,ingress.annotations."kubernetes\.io/ingress\.class"=traefik,ingress.hosts={kibana.eks-lab.com},env.ELASTICSEARCH_URL=http://elasticsearch-client:9200,image.tag=6.6.2,service.externalPort=80 stable/kibana


Output, after installing the chart.

```bash
NOTES:
To verify that kibana has started, run:

  kubectl --namespace=logging get pods -l "app=kibana"

Kibana can be accessed:

  * From outside the cluster, run these commands in the same shell:

    export NODE_PORT=$(kubectl get --namespace logging -o jsonpath="{.spec.ports[0].nodePort}" services kibana)
    export NODE_IP=$(kubectl get nodes --namespace logging -o jsonpath="{.items[0].status.addresses[0].address}")
    echo http://$NODE_IP:$NODE_PORT
```

##### Fluent Bit (daemonset)

[Stable helm Chart](https://hub.kubeapps.com/charts/stable/fluent-bit)

Install **Fluent Bit** helm chart

    helm install --name fluentbit --namespace logging --set backend.type=es,backend.es.host=elasticsearch-client,service.logLevel=info,filter.mergeJSONLog=false stable/fluent-bit

Output, after installing the chart.

```bash
NOTES:
fluent-bit is now running.

It will forward all container logs to the svc named elasticsearch-client on port: 9200

```

> To verify the installation, check if the pods are connected to the backend (elasticsearch) via logs. `kubectl logs -n logging pods/fluentbit-fluent-bit-wjhsr`

#### Local DNS

- Get the `External IP` assigned to the desired ingress service

        kubectl get services --all-namespaces

  ```bash
  NAMESPACE     NAME                            TYPE           CLUSTER-IP            EXTERNAL-IP                                                                    PORT(S)                                     AGE
  kube-system   tiller-deploy                   ClusterIP           172.20.224.93    <none>                                                                         44134/TCP                                   1h
  kube-system   traefik-ingress                 LoadBalancer        172.20.158.115        a4844b62951fa11e99e960a076ad3b79-1619440693.eu-west-1.elb.amazonaws.com        80:30499/TCP,443:31978/TCP,8080:31284/TCP   1h
  ```


- Get the **IP Addresses** assigned the load-balancer created by the ingress controller, using the command `host`

      host a4844b62951fa11e99e960a076ad3b79-1619440693.eu-west-1.elb.amazonaws.com

    ```bash
    a4844b62951fa11e99e960a076ad3b79-1619440693.eu-west-1.elb.amazonaws.com     has address 52.212.18.149
    a4844b62951fa11e99e960a076ad3b79-1619440693.eu-west-1.elb.amazonaws.com     has address 54.77.108.214
    a4844b62951fa11e99e960a076ad3b79-1619440693.eu-west-1.elb.amazonaws.com     has address 34.243.105.205
    ```

- Create or modify the entries into the local host file `/etc/hosts`

  ```bash
  52.212.18.149 grafana.eks-lab.com
  52.212.18.149 prometheus.eks-lab.com
  52.212.18.149 alertmanager.eks-lab.com
  52.212.18.149 elasticsearch.eks-lab.com
  52.212.18.149 kibana.eks-lab.com
  ```

- Using sed to replace previous configurations

        # Linux Machines
        sudo sed -i 's/34.253.164.67/5.176.104.136/g' /etc/hosts

        # In MacOS use gnu-sed instead
        brew install gnu-sed
        sudo gsed -i 's/5.176.104.136/35.176.104.136/g' /etc/hosts

## Graphs

Terraform already contains a ‘graph’ command for outputting tf plans. The advised utility for converting these outputs into a graphical format is a tool called `Graphviz`.

Firstly, we need to install the tool and other dependencies:

        sudo apt install graphviz python-pydot python-pydot-ng python-pyparsing \
        libcdt5 libcgraph6 libgvc6 libgvpr2 libpathplan4

Next, we need to generate the graphic file.

        # Export to PNG format
        terragrunt graph | dot -Tpng > graph.png

        # Export to svg format
        terragrunt graph | dot -Tsvg > graph.svg

> Depth can be configured using parameter `-module-depth=n`

## References

- [Naming conventions](http://lloydholman.co.uk/in-the-wild-aws-iam-naming-conventions/)
- [Traefik and Services](https://www.jeffgeerling.com/blog/2018/fixing-503-service-unavailable-and-endpoints-not-available-traefik-ingress-kubernetes)
- [Amazon EKS Ingress Guide](https://medium.com/@dmaas/amazon-eks-ingress-guide-8ec2ec940a70)