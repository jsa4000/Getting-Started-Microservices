# Terraform (HashiCorp)

## Introduction

### HashiCorp

**HashiCorp** provides a suite of open source tools intended to support development and deployment of large-scale service-oriented software installations. Each tool is aimed at specific stages in the life cycle of a software application, with a focus on automation. Many have a plugin-oriented architecture in order to provide integration with third-party technologies and services. Additional proprietary features for some of these tools are offered commercially and are aimed at enterprise customers.

The main product line consists of these following tools:

- **Vagrant**: supporting the building and maintenance of reproducible software development environments via virtualization technology.
- **Packer**: a tool for building virtual machine images for later deployment.
- **Terraform**: infrastructure as code software which enables provisioning and adapting virtual infrastructure across all major cloud providers.
- **Consul**: providing distributed KV storage, DNS based service discovery, RPC, and event propagation. The underlying event, membership, and failure detection mechanisms are provided by Serf, an open-source library also published by HashiCorp.
- **Vault**: which provides secrets management, identity-based access, and encrypting application data for auditing of secrets for applications, systems, users.
- **Nomad**: supporting scheduling and deployment of tasks across worker nodes in a cluster.

### Terraform

HashiCorp Terraform enables you to safely and predictably create, change, and improve infrastructure. It is an open source tool that codifies APIs into declarative configuration files that can be shared amongst team members, treated as code, edited, reviewed, and versioned.

Define infrastructure as code to increase operator productivity and transparency.

#### Infrastructure as Code

Infrastructure is described using a high-level configuration syntax. This allows a blueprint of your datacenter to be versioned and treated as you would any other code. Additionally, infrastructure can be shared and re-used.

#### Execution Plans

Terraform has a "planning" step where it generates an execution plan. The execution plan shows what Terraform will do when you call apply. This lets you avoid any surprises when Terraform manipulates infrastructure.

#### Resource Graph

Terraform builds a graph of all your resources, and parallelizes the creation and modification of any non-dependent resources. Because of this, Terraform builds infrastructure as efficiently as possible, and operators get insight into dependencies in their infrastructure.

#### Change Automation

Complex changesets can be applied to your infrastructure with minimal human interaction. With the previously mentioned execution plan and resource graph, you know exactly what Terraform will change and in what order, avoiding many possible human errors.

#### Providers

Since the agnostic capabilities of **Terraform**, it integrates with a large number of [Providers](https://www.terraform.io/docs/providers/)

- **AWS**: [AWS](https://www.terraform.io/docs/providers/aws/index.html)
- **Azure**: [Azure](https://www.terraform.io/docs/providers/azurerm/index.html)
- **Heroku**: [Heroku](https://www.terraform.io/docs/providers/heroku/index.html)
- **Google Cloud**: [Google Cloud](https://www.terraform.io/docs/providers/google/index.html)
- **OpenTeleokmCloud**: [OpenTeleokmCloud](https://www.terraform.io/docs/providers/opentelekomcloud/index.html)
- **Kubernetes**: [Kubernetes](https://www.terraform.io/docs/providers/kubernetes/index.html)

### Infrastructure as Code (IaC)

Infrastructure as code (IaC) is the process of managing and provisioning computer data centers through machine-readable definition files, rather than physical hardware configuration or interactive configuration tools. The IT infrastructure meant by this comprises both physical equipment such as bare-metal servers as well as virtual machines and associated configuration resources. The definitions may be in a version control system. It can use either scripts or declarative definitions, rather than manual processes, but the term is more often used to promote declarative approaches.

Infrastructure as code approaches are promoted for cloud computing, which is sometimes marketed as infrastructure as a service (IaaS). IaC supports IaaS, but should not be confused with it.

### Name Convention

- Default Pattern Format

    {vpc}-{RegionCode}-{EnvironmentCode}-{ApplicationStackCode}

- Default Pattern Components

    **RegionCode**

        (ue1|uw1|uw2|ew1|ec1|an1|an2|as1|as2|se1) for us-east-1, us-west-1, us-west-2, eu-west-1, eu-central-1, ap-northeast-1, ap-northeast-2, ap-southeast-1, ap-southeast-2, sa-east-1.

    **EnvironmentCode**

        (d|t|s|p) for development, test, staging, production.

    **ApplicationCode**

        ([a-z0-9\-]+) for the application stack that runs within the VPC (e.g. bid-data-app-stack).

- Default Pattern Examples

        vpc-us-east-1-p-bid-data-app-stack
        vpc-us-west-2-p-web-app-stack

Other Resources:

- [Cloud Resource Naming Conventions](https://confluence.huit.harvard.edu/display/CLA/Cloud+Resource+Naming+Conventions)
- [Azure Cloud Naming Convention](https://docs.microsoft.com/en-gb/azure/virtual-machines/windows/infrastructure-example)

## Installation

Following are the Steps in orde to install and configure Terraform

- Create a SSH Key (configuration by default)

    ssh-keygen

    > This is neccesary to prevent the error: openstack_compute_keypair_v2.keypair: 1 error(s) occurred:

- Install Terraform.

        sudo apt-get install unzip
        wget https://releases.hashicorp.com/terraform/0.11.7/terraform_0.11.7_linux_amd64.zip
        unzip terraform_0.11.7_linux_amd64.zip

- Add terraform into PATH environment variable (.profile)

        vi ~/.profile
        export PATH=$PATH:~/

  Or
  
        sudo mv terraform /usr/bin

- Validate the installation

        terraform version

    ```txt
    Terraform v0.11.7
        + provider.openstack v1.6.0
    ```

- Clone this repository via

        git clone https://github.com/OpenTelekomCloud/terraform-otc.git

- Switch to terraform directory

        cd terraform-otc/minimal

- Initialize Terraform provider via

        terraform init.

- Insert your login information into **parameter.tvars** (see next section).

- Check if everything looks fine with

        terraform plan -var-file=parameter.tvars

- Apply the changes via

        terraform apply -var-file=parameter.tvars

Other Resources:

- [OpenTelekomCloud GitHub](https://github.com/OpenTelekomCloud/terraform-otc)
- [Terraform Provider OpenTelekomCloud GitHub](https://github.com/terraform-providers/terraform-provider-opentelekomcloud)

> OpenTelekomCloud GitHub it is more updated apparently.

## Process

After installing Terraform, it is neccesary to create the *tf* files neccesary to create the Cloud Infraestructure, such as networks, elb, ecs, securety groups, etc.. This is done by each provider separately. All definitions are well documented in Terraform webpage.

### Configuration

All variables that can be changed are documented in *variables.tf*. There are reasonable default values for most of the variables in this file. Every variable may be overwritten by passing command line arguments to the Terraform invocation, or simply by passing a parameter file via the *-var-file* command line parameter. All the examples provide a file parameters.tvars containing all necessary parameters for the example to work (for example your login information). You can add more parameters for any variable existing in the *variables.tf* file. For more information see Terraform variables documentation.

### Authentication

To get the examples to work with you OTC account, you need three information bits:

    username (user name from My Credential)
    password (your login password)
    domain_name (Domain Name from My Credential)

### Terraform files

- *parameters.tvars*

This file overrides the default values in *variables.tf*.

```java
username = "MyUserNAme"
password = "MyPassword"
domain_name = "MyDomainName"

project_name  = "MyProjectName"

key_pair = "KeyPair-4c14"
tenant_name = "eu-de"
endpoint = "https://iam.eu-de.otc.t-systems.com:443/v3"

flavor_name = "s2.medium.1"
image_name  = "Standard_Debian_9_latest"

security_group = "4f71fdaa-b069-4189-a95f-a283c8ed620c"
fixed_ip = "10.20.163.199"
subnet_cidr = "10.20.163.0/24"
network_uuid = "345a8e5a-5c0c-4058-a85c-2a37e89a2eed"
```

- *variables.tf*

This file defines and set default values that will be used along the creation.

```cs
### OpenStack Credentials
variable "username" {}

variable "password" {}

variable "domain_name" {}

variable "key_pair" {}

variable "tenant_name" {}

variable "endpoint" {}

### OTC Specific Settings
variable "external_network" {
  default = "admin_external_net"
}

### Project Settings
variable "project_name" {
  default = "myProjectName"
}

variable "subnet_cidr" {
  default = "10.20.163.0/24"
}

variable "ssh_pub_key" {
  default = "~/.ssh/id_rsa.pub"
}

### VM (Instance) Settings

variable "security_group" {}

variable "network_uuid" {}

variable "fixed_ip" {}

variable "instance_count" {
  default = "1"
}

variable "flavor_name" {
  default = "s2.medium.1"
}

variable "image_name" {
  default = "Standard_CentOS_7_latest"
}

```

The way these variables are used are:

```cs
  password    = "${var.password}"
  tenant_name = "${var.tenant_name}"
  domain_name = "${var.domain_name}"
```

- *provider.tf*

Defines the specfic provider to use. It can be openstack, opentelekomcloud, aws, azure, etc..

```cs
provider "openstack" {
  user_name   = "${var.username}"
  password    = "${var.password}"
  tenant_name = "${var.tenant_name}"
  domain_name = "${var.domain_name}"
  auth_url    = "${var.endpoint}"
}
```

- *instances.tf*

This file defines the template and the way to create ecs.

```cs
resource "openstack_compute_instance_v2" "webserver" {
  count           = "${var.instance_count}"
  name            = "${var.project_name}-ecs${format("%02d", count.index+1)}"
  image_name      = "${var.image_name}"
  flavor_name     = "${var.flavor_name}"
  key_pair        = "${var.key_pair}"
  network {
    uuid = "${var.network_uuid}"
  }
  security_groups = ["${var.security_group}"]
}
```

In order to create instances, previous steps and dependencies could be needed to be created previously. This is done with the parameter *depends_on*.

Following example show the way an instance is created setting a dependency, from another resource.

```cs
resource "openstack_compute_instance_v2" "webserver" {
  count           = "${var.instance_count}"
  name            = "${var.project}-webserver${format("%02d", count.index+1)}"
  image_name      = "${var.image_name}"
  flavor_name     = "${var.flavor_name}"
  key_pair        = "${openstack_compute_keypair_v2.keypair.name}"
  network {
    uuid = "${openstack_networking_network_v2.network.id}"
  }
  depends_on      = ["openstack_networking_router_interface_v2.interface"]
}

resource "openstack_compute_floatingip_associate_v2" "webserver_fip" {
  floating_ip           = "${openstack_networking_floatingip_v2.fip.address}"
  instance_id           = "${openstack_compute_instance_v2.webserver.id}"
  wait_until_associated = "true"
}
```

### Execute

All terraform files (tf) contained inside the folder are loaded when Terraform initializes.

    /Example/
        |- instances.tf
        |- parameters.tvars
        |- provider.tf
        |- instances.tf
        |- ...

- Initialize Terrafom

        terraform init

- Check Plan to be executed by terraform (depending on the previous files lodaded)

        terraform plan -var-file=parameter.tvars -lock=false

    > Use *-lock=false* if getting an error due locking state.

- Execute previous plan.

        terraform apply -var-file=parameter.tvars -lock=false

    > Terraform request a final confirmation to applyt the changes. It must be typed "yes"

- This is the Output

```txt
vagrant@ubuntu-xenial64:~/terraform-otc/example$ terraform apply -var-file=parameter.tvars -lock=false
openstack_compute_instance_v2.webserver: Refreshing state... (ID: f0c1deff-bfa2-41a7-8802-ad0b6956d346)

An execution plan has been generated and is shown below.
Resource actions are indicated with the following symbols:
  + create

Terraform will perform the following actions:

  + openstack_compute_instance_v2.webserver
      id:                        <computed>
      access_ip_v4:              <computed>
      access_ip_v6:              <computed>
      all_metadata.%:            <computed>
      availability_zone:         <computed>
      flavor_id:                 <computed>
      flavor_name:               "s2.medium.1"
      force_delete:              "false"
      image_id:                  <computed>
      image_name:                "Standard_Debian_9_latest"
      key_pair:                  "KeyPair-4c14"
      name:                      "MyProjectName-ecs01"
      network.#:                 "1"
      network.0.access_network:  "false"
      network.0.fixed_ip_v4:     <computed>
      network.0.fixed_ip_v6:     <computed>
      network.0.floating_ip:     <computed>
      network.0.mac:             <computed>
      network.0.name:            <computed>
      network.0.port:            <computed>
      network.0.uuid:            "345a8e5a-5c0c-4058-a85c-2a37e89a2eed"
      region:                    <computed>
      security_groups.#:         "1"
      security_groups.920147633: "4f71fdaa-b069-4189-a95f-a283c8ed620c"
      stop_before_destroy:       "false"


Plan: 1 to add, 0 to change, 0 to destroy.

Do you want to perform these actions?
  Terraform will perform the actions described above.
  Only 'yes' will be accepted to approve.

  Enter a value: yes

openstack_compute_instance_v2.webserver: Creating...
  access_ip_v4:              "" => "<computed>"
  access_ip_v6:              "" => "<computed>"
  all_metadata.%:            "" => "<computed>"
  availability_zone:         "" => "<computed>"
  flavor_id:                 "" => "<computed>"
  flavor_name:               "" => "s2.medium.1"
  force_delete:              "" => "false"
  image_id:                  "" => "<computed>"
  image_name:                "" => "Standard_Debian_9_latest"
  key_pair:                  "" => "KeyPair-4c14"
  name:                      "" => "MyProjectName-ecs01"
  network.#:                 "" => "1"
  network.0.access_network:  "" => "false"
  network.0.fixed_ip_v4:     "" => "<computed>"
  network.0.fixed_ip_v6:     "" => "<computed
openstack_compute_instance_v2.webserver: Still creating... (10s elapsed)
openstack_compute_instance_v2.webserver: Still creating... (20s elapsed)
openstack_compute_instance_v2.webserver: Still creating... (30s elapsed)
openstack_compute_instance_v2.webserver: Still creating... (40s elapsed)
openstack_compute_instance_v2.webserver: Still creating... (50s elapsed)
openstack_compute_instance_v2.webserver: Still creating... (1m0s elapsed)
openstack_compute_instance_v2.webserver: Still creating... (1m10s elapsed)
openstack_compute_instance_v2.webserver: Still creating... (1m20s elapsed)
openstack_compute_instance_v2.webserver: Still creating... (1m30s elapsed)
openstack_compute_instance_v2.webserver: Still creating... (1m40s elapsed)
openstack_compute_instance_v2.webserver: Still creating... (1m50s elapsed)
openstack_compute_instance_v2.webserver: Still creating... (2m0s elapsed)
openstack_compute_instance_v2.webserver: Still creating... (2m10s elapsed)
openstack_compute_instance_v2.webserver: Still creating... (2m20s elapsed)
openstack_compute_instance_v2.webserver: Creation complete after 2m26s (ID: 1ee1ba08-49c6-4142-bee0-ab2c37423d88)
```

### Modules

Instead of having one single *parameters.tvars* with all the values, Terraform provides a way to create **modules** in order to define the source and the variables depending on the environment or infraestructure to create.

This is the structure

    - main.tf
    - variables.tf
    |-dev
      |- instances.tf
      |- services.tf
      |- networks.tf
      |- provider.tf
    |-prod
      |- instances.tf
      |- services.tf
      |- networks.tf
      |- provider.tf
      |- loadbalancers.tf
      |- floatingips.tf

> Since *develop* and *production* environments are different due requeriments: replication factor, balancing, etc.. However, the Terrafrom template files (tf) could be re-used, this is done by using the **modules**.

- *main.tf*

    ```cs
    module "do-dev" {
      source = "./dev"
      do_token = "${var.do_token}"
      k8s-token = "${var.kubeadm_token}"
      private_key_file = "${var.private_key_file}"
      public_key_file = "${var.public_key_file}"

    }

    module "gb-prod" {
      source = "./prod"
      frontend_image = "${var.fe_image}"
      frontend_replicas = "${var.fe_replicas}"
      kube_config_file = "${module.do-k8s-cluster.kube_config_file}"
    }
    ```

- *variables.tf*

  ```cs
  variable "fe_replicas" {
    description = "replicas for frontend"
    default = 3
  }

  variable "fe_image"{
    description = "frontend_image"
    default = "gcr.io/google_samples/gb-frontend:v3"
  }

  variable "do_token" {
    description = "digital ocean auth token"
  }

  variable "kubeadm_token" {
    description = "kubeadm auth token"
  }

  variable "private_key_file" {
    description = "Location of Private Key file"
  }

  variable "public_key_file" {
    description = "location of public key file"
  }
  ```

Following commands must be set to provision the cluster:

- Terraform picks up all modules

      terraform get  

- Terraform will pull all required plugins

      terraform init

- validate whether everything shall run as expected

      terraform plan

- Finally use the desired module

      terraform apply -target=module.do-dev

### References

- [OpenStack](https://www.terraform.io/docs/providers/openstack/)
- [Create ESC Instance](https://www.terraform.io/docs/providers/openstack/r/compute_instance_v2.html)
- [Security Group](https://www.terraform.io/docs/providers/openstack/r/compute_secgroup_v2.html)
- [Terraform - Best Practices](http://www.anniehedgie.com/terraform-and-azure)
- [Terraform - How to](https://blog.gruntwork.io/how-to-create-reusable-infrastructure-with-terraform-modules-25526d65f73d)
- [Terraform Advanced Techniques](https://blog.gruntwork.io/terraform-tips-tricks-loops-if-statements-and-gotchas-f739bbae55f9)
