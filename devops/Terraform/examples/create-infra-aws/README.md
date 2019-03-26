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

- Awscli

        sudo apt-get install awscli

- Validate the installation
  
        terraform version
        terragrunt --version
        awscli --version

## Terraform

Add your aws credentials to the environment variables.

> You can use other tool like `aws-iam-authenticator` to manage.

```bash
export AWS_ACCESS_KEY_ID=AKIAIDQQKGRFTSH2A
export AWS_SECRET_ACCESS_KEY=002USSrL3EgBZJQaf5h6xnwFpdy
export AWS_DEFAULT_REGION=eu-west-1
```

Once we have terraform IAM account created we can proceed to next step creating dedicated `S3 bucket` to keep terraform state files (`.tfstate`)

    aws s3 mb s3://terraform-state-lab
    aws s3 mb s3://terraform-state-lab --region eu-west-1

Enable versioning on the newly created bucket

    aws s3api put-bucket-versioning --bucket terraform-state-lab --versioning-configuration Status=Enabled

## References

- [Naming conventions](http://lloydholman.co.uk/in-the-wild-aws-iam-naming-conventions/)