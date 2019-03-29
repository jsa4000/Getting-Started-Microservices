variable "region" {
  description = "AWS region to launch servers."
}

variable "owner" {
  description = "Owner of the current environment"
}

variable "environment" {
  description = "Current environment of the cluster to be deployed"
}

variable "cluster_name" {
  description = "Cluster name given to the whole VPC"
}

variable "cidr_subnet_bits" {
  description = "Bits to add to new subnets created"
  default = 8
}

variable "cidr_block" {
  description = "CIDR for the whole VPC"
}

variable "db_identifier" {
   description = "Database identifier"
}

variable "db_storage_type" {
   description = "Database storage type"
}

variable "db_allocated_storage" {
   description = "Database allocated storage"
}

variable "db_port" {
   description = "Database port to use"
}

variable "db_engine" {
   description = "Database engine"
}

variable "db_family" {
   description = "Database parameter group family"
}

variable "db_engine_version" {
   description = "Database engine version"
}

variable "db_instance_class" {
   description = "Database storage type"
}

variable "db_username" {
   description = "Database root username"
}

variable "db_password" {
   description = "Database password root"
}

variable "eks_instance_type" {
   description = "Worker instance type yo use"
}

variable "eks_asg_desired_capacity" {
   description = "Desired capacity for auto-scaling on-demand workers"
}

variable "eks_asg_spot_desired_capacity" {
   description = "Desired capacity for auto-scaling spot fleet workers"
}

variable "bastion_instance_type" {
  description = "Instance type used for the bastion ec2"
  default = "t2.micro"
}

variable "bastion_ami" {
    description = "Image used for the bastion ec2"
    default = "ami-969ab1f6"
}

variable "bastion_key_name" {}

variable "bastion_public_key" {}


