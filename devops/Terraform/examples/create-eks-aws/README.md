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

## Terraform

- Add your aws credentials to the environment variables.

        ```bash
        export AWS_ACCESS_KEY_ID=..
        export AWS_SECRET_ACCESS_KEY=..
        export AWS_DEFAULT_REGION=eu-west-1
        ```

- Once we have terraform IAM account created we can proceed to next step creating dedicated `S3 bucket` to keep terraform state files (`.tfstate`)

        aws s3 mb s3://terraform-state-lab
        aws s3 mb s3://terraform-state-lab --region eu-west-1

- Enable versioning on the newly created bucket

        aws s3api put-bucket-versioning --bucket terraform-state-lab --versioning-configuration Status=Enabled

- Check the bucket has been created successfully

        aws s3api list-buckets --query "Buckets[].Name"

- Delete bucket

        aws s3 rm s3://terra-state-bucket --recursive

        aws s3api put-bucket-versioning --bucket terra-state-bucket --versioning-configuration Status=Suspended

        aws s3api delete-objects --bucket terra-state-bucket --delete \
        "$(aws s3api list-object-versions --bucket terra-state-bucket | \
        jq '{Objects: [.Versions[] | {Key:.Key, VersionId : .VersionId}], Quiet: false}')"

        aws s3 rb s3://terra-state-bucket --force

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

> Use `TF_WARN_OUTPUT_ERRORS=1 terragrunt destroy`if you find any output or warning after the execution.

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

        terragrunt output eks_kubectl_config

3. Add output of terragrunt output kubeconfig” to ~/.kube/config-devel

        mkdir ~/.kube
        terragrunt output eks_kubectl_config > ~/.kube/config-devel

        # Set the context with the kubeconfig to use with kubectl
        # Note AWS crentials must be provided
        export KUBECONFIG=$KUBECONFIG:~/.kube/config-devel

4. Verify kubectl connectivity

        kubectl get namespaces
        kubectl get services

        # This should be prompt 'No resources found.' for the next step
        kubectl get nodes

5. Second part we need to allow EKS to add nodes by running `configmap`

        terragrunt output eks_config_map_aws_auth > config_map_aws_auth.yaml
        kubectl apply -f config_map_aws_auth.yaml

6. Now you should be able to see nodes

        kubectl get nodes -o wide

        NAME                                        STATUS   ROLES    AGE   VERSION   INTERNAL-IP   EXTERNAL-IP   OS-IMAGE         KERNEL-VERSION                CONTAINER-RUNTIME
        ip-10-10-0-243.eu-west-1.compute.internal   Ready    <none>   3m    v1.11.9   10.10.0.243   <none>        Amazon Linux 2   4.14.104-95.84.amzn2.x86_64   docker://18.6.1
        ip-10-10-1-191.eu-west-1.compute.internal   Ready    <none>   3m    v1.11.9   10.10.1.191   <none>        Amazon Linux 2   4.14.104-95.84.amzn2.x86_64   docker://18.6.1
        ip-10-10-1-52.eu-west-1.compute.internal    Ready    <none>   3m    v1.11.9   10.10.1.52    <none>        Amazon Linux 2   4.14.104-95.84.amzn2.x86_64   docker://18.6.1
        ip-10-10-2-227.eu-west-1.compute.internal   Ready    <none>   3m    v1.11.9   10.10.2.227   <none>        Amazon Linux 2   4.14.104-95.84.amzn2.x86_64   docker://18.6.1

### Tools

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
h
## References

- [Naming conventions](http://lloydholman.co.uk/in-the-wild-aws-iam-naming-conventions/)