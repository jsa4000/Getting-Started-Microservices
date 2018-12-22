# Installation

- Go to the desired **environment** folder to deploy (where main terraform files are placed)

        cd deployment/eu-west-1/development
        cd deployment/eu-west-1/demo

- Initialize providers in terraform and download dependencies, modules and sources.
  
        terraform init

- Execute a **plan** to verify all the files are correct
  
        terraform plan

    > This command parse and verify all dependencies and resources are correct.
  
- Execute **apply** to finalize the deployment or update
  
        terraform apply

- The state is stored in that particular folder
 
    >  Use terraform back-end and ``terragrunt`` to store the states into a remote storage using the same key-path