terragrunt = {
  remote_state {
    backend = "s3"

    config {
      bucket        = "terraform-state-lab" 
      key           = "${path_relative_to_include()}/terraform.tfstate"
      region        = "eu-west-1"
      encrypt       = true
    }
  }

  terraform {
    extra_arguments "retry_lock" {
      commands = [
        "init",
        "apply",
        "refresh",
        "import",
        "plan",
        "taint",
        "untaint"
      ]
      arguments = [
        "-lock-timeout=10m"
      ]
    }
  }
}