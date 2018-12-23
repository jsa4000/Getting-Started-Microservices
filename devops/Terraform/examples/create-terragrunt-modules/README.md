# Terrarform + Terragrunt

## Installation

- Terrafrom (See instructions)

- Terragrunt (v0.17.3)

        cd /tmp
        wget https://github.com/gruntwork-io/terragrunt/releases/download/v0.17.3/terragrunt_linux_amd64
        sudo mv terragrunt_linux_amd64 /usr/bin/terragrunt
        sudo chmod a+wrx /usr/bin/terragrunt

## Project

This project supports the following structure

```txt
└───deployment
    ├───eu-west-1
    │   │   common.tfvars
    │   │   terraform.tfvars
    │   │
    │   ├───demo
    │   │       override.tfvars
    │   │       remote_state.tf
    │   │       terraform.tfvars
    │   │
    │   ├───development
    │   │       override.tfvars
    │   │       remote_state.tf
    │   │       terraform.tfvars
    │   │
    │   └───staging
    │           override.tfvars
    │           remote_state.tf
    │           terraform.tfvars
    │
    └───us-east-1
```

## Deployment

- Install the environment variables with the credentials (AWS, openstack, etc..)

        source demo-openrc.sh
        # Unset OS_USER_DOMAIN_NAME or OS_PROJECT_DOMAIN_ID variable
        unset OS_PROJECT_DOMAIN_ID

   > Previous script will prompt to ask for the ``password``.

- Go to the desired **environment** folder to deploy (where main terraform files are placed)

        cd deployment/eu-west-1/development
        cd deployment/eu-west-1/demo

- Initialize terragrunt. (Delete cache and download dependencies, modules and sources)
  
        terragrunt get --terragrunt-source-update

```powershell
vagrant@terraform-master:/vagrant/files/create-terragrunt-modules/deployment/eu-west-1/development$ terragrunt get --terragrunt-source-update

[terragrunt] [/vagrant/files/create-terragrunt-modules/deployment/eu-west-1/development] 2018/12/23 16:49:45 Running command: terraform --version
[terragrunt] 2018/12/23 16:49:45 Reading Terragrunt config file at /vagrant/files/create-terragrunt-modules/deployment/eu-west-1/development/terraform.tfvars
[terragrunt] 2018/12/23 16:49:45 The --terragrunt-source-update flag is set, so deleting the temporary folder /vagrant/files/create-terragrunt-modules/deployment/eu-west-1/development/.terragrunt-cache/PAPxAtQfq57DKbyP3qDJbjDaB-8/MK85byGxFr6VrceQTaiGHr8nncw before downloading source.
[terragrunt] 2018/12/23 16:49:51 Downloading Terraform configurations from git::https://github.com/jsa4000/Getting-Started-Microservices.git into /vagrant/files/create-terragrunt-modules/deployment/eu-west-1/development/.terragrunt-cache/PAPxAtQfq57DKbyP3qDJbjDaB-8/MK85byGxFr6VrceQTaiGHr8nncw using terraform init
[terragrunt] [/vagrant/files/create-terragrunt-modules/deployment/eu-west-1/development] 2018/12/23 16:49:51 Initializing remote state for the swift backend
[terragrunt] [/vagrant/files/create-terragrunt-modules/deployment/eu-west-1/development] 2018/12/23 16:49:51 Running command: terraform init -backend-config=container=deployment-eu-west-1-terraform-state -backend-config=key=development/terraform.tfstate -lock-timeout=10m -get=false -get-plugins=false -backend=false -from-module=git::https://github.com/jsa4000/Getting-Started-Microservices.git -no-color /vagrant/files/create-terragrunt-modules/deployment/eu-west-1/development/.terragrunt-cache/PAPxAtQfq57DKbyP3qDJbjDaB-8/MK85byGxFr6VrceQTaiGHr8nncw
Copying configuration from "git::https://github.com/jsa4000/Getting-Started-Microservices.git"...
Terraform initialized in an empty directory!

The directory has no Terraform configuration files. You may begin working
with Terraform immediately by creating Terraform configuration files.
[terragrunt] 2018/12/23 16:50:09 Copying files from /vagrant/files/create-terragrunt-modules/deployment/eu-west-1/development into /vagrant/files/create-terragrunt-modules/deployment/eu-west-1/development/.terragrunt-cache/PAPxAtQfq57DKbyP3qDJbjDaB-8/MK85byGxFr6VrceQTaiGHr8nncw/devops/Terraform/examples/create-terraform-modules/module/shared/openstack
[terragrunt] 2018/12/23 16:50:09 Setting working directory to /vagrant/files/create-terragrunt-modules/deployment/eu-west-1/development/.terragrunt-cache/PAPxAtQfq57DKbyP3qDJbjDaB-8/MK85byGxFr6VrceQTaiGHr8nncw/devops/Terraform/examples/create-terraform-modules/module/shared/openstack
[terragrunt] [/vagrant/files/create-terragrunt-modules/deployment/eu-west-1/development] 2018/12/23 16:50:09 Initializing remote state for the swift backend
[terragrunt] [/vagrant/files/create-terragrunt-modules/deployment/eu-west-1/development] 2018/12/23 16:50:09 Running command: terraform init -backend-config=container=deployment-eu-west-1-terraform-state -backend-config=key=development/terraform.tfstate -lock-timeout=10m
Initializing modules...
- module.create_openstack_compute_01
Getting source "../../provider/openstack/compute"
- module.create_openstack_compute_02
Getting source "../../provider/openstack/compute"
- module.create_openstack_dns
Getting source "../../provider/openstack/dns"
- module.create_openstack_network
Getting source "../../provider/openstack/network"
- module.create_openstack_storage
Getting source "../../provider/openstack/storage"

Initializing the backend...

Successfully configured the backend "swift"! Terraform will automatically
use this backend unless the backend configuration changes.

Initializing provider plugins...
- Checking for available provider plugins on https://releases.hashicorp.com...
- Downloading plugin for provider "openstack" (1.13.0)...

The following providers do not have any version constraints in configuration,
so the latest version was installed.

To prevent automatic upgrades to new major versions that may contain breaking
changes, it is recommended to add version = "..." constraints to the
corresponding provider blocks in configuration, with the constraint strings
suggested below.

* provider.openstack: version = "~> 1.13"

Terraform has been successfully initialized!

You may now begin working with Terraform. Try running "terraform plan" to see
any changes that are required for your infrastructure. All Terraform commands
should now work.

If you ever set or change modules or backend configuration for Terraform,
rerun this command to reinitialize your working directory. If you forget, other
commands will detect it and remind you to do so if necessary.
[terragrunt] 2018/12/23 16:50:13 Running command: terraform get
- module.create_openstack_compute_01
- module.create_openstack_compute_02
- module.create_openstack_dns
- module.create_openstack_network
- module.create_openstack_storage
```

- Modules can be deployed separately (see the previous modules imported using ``--terragrunt-source-update``)

        terragrunt plan --target=module.create_openstack_network

- Execute a **plan** to verify all the files are correct
  
        terragrunt plan

    > This command parse and verify all dependencies and resources are correct.
  
- Execute **apply** to finalize the deployment or update
  
        terragrunt apply

- It creates the desired state (``tfstate.tf``) into the object storage confgiured

        deployment-eu-west-1-terraform-state