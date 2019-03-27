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

environment           = "development"
cidr_block            = "10.10.0.0/16"
cluster_name          = "eks-lab-dev"

db_identifier         = "eks-lab-dev"
db_storage_type       = "gp2"
db_allocated_storage  = "10"
db_engine             = "postgres"
db_engine_version     = "9.6.6"
db_family             = "postgres9.6"
db_instance_class     = "db.t2.micro"
db_port               = 5432
db_username           = "root"
db_password           = "password"

eks_instance_type             = "t2.small"
eks_asg_desired_capacity      = 2
eks_spot_instance_pools       = 10
eks_worker_group_count        = 1
eks_worker_group_launch_count = 1

