terragrunt = {
  include {
    path = "${find_in_parent_folders()}"
  }

  terraform {
    source = "../../../modules/aws"

    extra_arguments "conditional_vars" {
      commands            = ["${get_terraform_commands_that_need_vars()}"]
      optional_var_files  = [
        "${get_parent_tfvars_dir()}/../common.tfvars",
        "${get_parent_tfvars_dir()}/override.tfvars"
      ]
      arguments = [
        "-var-file=terraform.tfvars"
      ]
    }
  }
}

# VPC
environment           = "development"
cidr_block            = "10.10.0.0/16"
cluster_name          = "eks-lab-dev"

# Database
db_identifier         = "eks-lab-dev-db"
db_storage_type       = "gp2"
db_allocated_storage  = "10"
db_engine             = "postgres"
db_engine_version     = "9.6.6"
db_family             = "postgres9.6"
db_instance_class     = "db.t2.micro"
db_port               = 5432
db_username           = "root"
db_password           = "password"

# EKS Cluster
eks_instance_type             = "t2.xlarge" #"t2.small"
eks_asg_desired_capacity      = 2
eks_asg_spot_desired_capacity = 2

# Bastion
bastion_instance_type = "t2.micro"
bastion_ami = "ami-07dc734dc14746eab"
bastion_key_name = "bastion-key"
bastion_public_key = "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAACAQC6GXU6aOvqHE0t+5EMuE2vDlFPqzTRZINpOr9iA400HYSbaRkEpk1wvtjzCRgeSEN0n47McrD8pgDSLQQ2OK7265aji+/FimbF7uadst9y2brGDmXIDLjdOj84fCztEAiabZfZpxa1rVHc4fmv/W6fx9Hb3xx+eT2SZFfWsbyoUhgDFPUWHbebcEBrN07bAQSm6QcoeOHRhokKotencuj36eUd/sy4jRO+fv1jG2/xeTkBrfaRqtEXNwQtix3QZ0BtgxHsPQ9uSvoUn5KBpkPTpmPy6nObBps98glP7EzxuUN1k33Fcu0JbYF4zVjKW2jxf8kSsN675bABfjk6NWm6KXwOb+R1kS8hD20uBVcSAxIBdZoXjguB7K+fS1Hgm60rVSO59bAmhKfCTH2gviMBf3QrI00piH5FSSkIuL0/LVI1oE20Z0mlglKgywBAWl0ynOWd5MHUOw8xYIEZqrMICFAjxugRBelH65boTP/PLsTbLPSFr+h3WH6wGausDXJH+k/+6v2WlYEML4UUd3Sjk8tGzMJxArIJFBCmEfRUm+N6m+YgJVpfQSLhpLU9pufPfpLyis+2LpDrPk/fWLPabh5YEQOsS0Q5qiyinHQAXARGv4j+bUr70RXXsjcYYziQHiDsm20JK8S9Bz7LenCwthY9I79GDFx4jSJBKATe3w== bastion@gmail.com"
