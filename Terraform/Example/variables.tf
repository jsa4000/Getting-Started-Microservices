### OpenStack Credentials
variable "username" {}

variable "password" {}

variable "domain_name" {} 

variable "key_pair" {} 

variable "tenant_name" {}

variable "endpoint" {}

### OTC Specific Settings
variable "external_network" {
  default = "admin_external_net"
}

### Project Settings
variable "project_name" {
  default = "myProjectName"
}

variable "subnet_cidr" {
  default = "10.20.163.0/24"
}

variable "ssh_pub_key" {
  default = "~/.ssh/id_rsa.pub"
}

### VM (Instance) Settings

variable "security_group" {}

variable "network_uuid" {}

variable "fixed_ip" {}

variable "instance_count" {
  default = "1"
}

variable "flavor_name" {
  default = "s2.medium.1"
}

variable "image_name" {
  default = "Standard_CentOS_7_latest"
}
