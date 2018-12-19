# Terraform Openstack deployment

#Debugging
#OS_DEBUG=1 
#TF_LOG=DEBUG

variable "auth_url"	{} 	
variable "tenant_name"	{} 	
variable "user_name"	{}	 	
variable "password"	{} 	
variable "domain_name"	{} 	 
variable "ssh_key_public"	{} 

provider "openstack" {
  user_name   = "${var.user_name}"
  password    = "${var.password}"
  tenant_name = "${var.tenant_name}"
  domain_name = "${var.domain_name}"
  auth_url    = "${var.auth_url}"
}

output "terraform-provider" {
    value = "Connected with OpenStack at ${var.auth_url}"	
}


