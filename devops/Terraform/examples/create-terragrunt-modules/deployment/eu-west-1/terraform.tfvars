terragrunt = {
  remote_state {
    backend = "swift"

    config {
      container          = "deployment-eu-west-1-tfstate"
      # Subfolders and keys are not supported using swift like S3 (nor dynamoDb) 
      # Refernce: https://www.terraform.io/docs/backends/types/swift.html
      #   (S3) key  = "${path_relative_to_include()}/terraform.tfstate"
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
