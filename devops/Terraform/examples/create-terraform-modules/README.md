# Installation

This project supports the following structure

```txt
├───deployment
│   ├───eu-west-1
│   │   ├───demo
│   │   ├───development
│   │   └───staging
│   └───us-east-1
│   │   ├───client-partner
└───module
    ├───provider
    │   ├───aws
    │   │   ├───vpc
    │   │   ├───ec2
    │   │   ├───route53
    │   │   └───s3
    │   ├───azure
    │   └───openstack
    │       ├───compute
    │       ├───dns
    │       ├───network
    │       └───storage
    └───shared
        ├───aws
        ├───azure
        └───openstack
```

## Deployment


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