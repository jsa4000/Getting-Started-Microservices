variable "environment" {}

variable "owner" {}

variable "cluster_name" {}

variable "vpc_id" {}

variable "subnets" {
  type = "list"
}

variable "identifier" {}

variable "storage_type" {}

variable "allocated_storage" {}

variable "engine" {}

variable "family" {}

variable "engine_version" {}

variable "instance_class" {}

variable "port" {}

variable "username" {
  default = "root"
}

variable "password" {}

