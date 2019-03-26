variable "zone_region" {
  description = "AWS region to launch servers."
  //default     = "eu-west-1"
}

variable "cluster_name" {
  description = "Cluster name given to the whole VPC"
}

variable "cidr_block" {
  description = "CIDR for the whole VPC"
}