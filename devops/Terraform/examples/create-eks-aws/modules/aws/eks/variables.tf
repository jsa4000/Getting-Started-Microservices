variable "environment" {}

variable "owner" {}

variable "cluster_name" {}

variable "vpc_id" {}

variable "sec_group" {
}

variable "instance_type" {}

variable "subnets" {
  type = "list"
}

variable "asg_desired_capacity" {
  default = 2
}

variable "spot_instance_pools" {
  default = 10
}

