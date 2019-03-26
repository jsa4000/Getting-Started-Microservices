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

zone_region = "eu-west-1"
cidr_block = "10.10.0.0/16"
cluster_name = "lab-dev"

