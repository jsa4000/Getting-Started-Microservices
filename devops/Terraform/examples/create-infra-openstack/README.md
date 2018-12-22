# Intructions

- The state will be stored locally as ``terraform.tfstate``
- In order to run with **terraform** use the following commands

        terraform init
        terraform apply -var-file=terraform.tfvars

    > Sometimes, and depending on randomness the deplyment can be fail because the order. Just run again the ``apply`` command.

- In order to destroy

        terraform destroy -var-file=terraform.tfvars