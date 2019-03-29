variable "environment" {}

variable "owner" {}

variable "cluster_name" {}

variable "vpc_id" {}

variable "instance_type" {
  default = "t2.micro"
}

variable "subnets" {
  type = "list"
}

variable "key_name" {}

variable "asg_desired_capacity" {
  default = 2
}

variable "asg_spot_desired_capacity" {
  default = 2
}

variable "spot_instance_pools" {
  default = 10
}

