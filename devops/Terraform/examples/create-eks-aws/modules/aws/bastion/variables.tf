variable "environment" {}

variable "owner" {}

variable "cluster_name" {}

variable "vpc_id" {}


variable "subnet_id" {}

variable "instance_type" {
  default = "t2.micro"
}

variable "key_name" {}

variable "public_key" {}
