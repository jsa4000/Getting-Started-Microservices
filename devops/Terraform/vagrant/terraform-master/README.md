# Terraform

## Installation

Following are the Steps in order to install and configure Terraform

- Create a SSH Key (configuration by default)

        ssh-keygen

    > This is needed to prevent the error: ``openstack_compute_keypair_v2.keypair: 1 error(s) occurred:``

- Download Terraform binary from [official website](https://www.terraform.io/downloads.html)

        sudo apt-get install unzip
        wget https://releases.hashicorp.com/terraform/0.11.11/terraform_0.11.11_linux_amd64.zip
        unzip terraform_0.11.11_linux_amd64.zip

- Add terraform into PATH environment variable (.profile)

        vi ~/.profile
        export PATH=$PATH:~/

  Or copy terraform binary into ``/usr/bin`` folder
  
        sudo mv terraform /usr/bin

- Validate the installation

        terraform version

    ```txt
    Terraform v0.11.11
        + provider.openstack v1.12.0
    ```

## First deployment

- Copy the example project ``create-compute-openstack`` into the shared folder ``terraform-master/files``
- From the virtual machine go to the root of that particular folder, terraform will take care of all *.tf* files recursively.
  
        cd /vagrant/files/create-compute-openstack/

- From **OpenStack** dashboard:
  1. Create new **project** (**tenant**) named ``test``.
  1. Add default ``demo`` and ``admin`` **users** to the project.
  1. Create new **Application Credentials** with the name demo. Download *.sh* and *.yaml* files. These files contains information about credentials, endpoints, etc..
  1. Create a new **Key-Pair** inside **test (project) -> Compute -> Key Pairs** called ``demo``. 
     > **This is not working commented in the computed instance.**
  1. Get the defaults (or create new resources) ``network_uuid`` and ``security_group``.

- Insert previous information in **parameter.tvars**. Use the documentation [page](https://www.terraform.io/docs/providers/openstack/) to verify all the parameters.

```bash
username = "demo"
tenant_name = "test"
password = "password"

domain_name = "Default"
project_name = "test"

key_pair = "demo"
endpoint = "http://10.0.0.10/identity/v3"

flavor_name = "m1.tiny"
image_name  = "cirros-0.3.6-x86_64-disk"

security_group = "5fd591f5-33dd-4ad0-badf-a4be5030fd75"
fixed_ip = "172.24.4.1"
subnet_cidr = "172.24.4.0/24"
network_uuid = "c7c7f9e6-0c25-48ba-9a67-ca755298b183"

```

- Check if everything looks fine with

        terraform plan -var-file=parameter.tvars

- Apply the changes via

        terraform apply -var-file=parameter.tvars

Other Resources:

- [OpenTelekomCloud GitHub](https://github.com/OpenTelekomCloud/terraform-otc)
- [Terraform Provider OpenTelekomCloud GitHub](https://github.com/terraform-providers/terraform-provider-opentelekomcloud)

