# Terraform Openstack 
# Author: Rafael Belchior - rafael.belchior@tecnico.ulisboa.pt

# Elemets of the cloud such as virtual servers,
# networks, firewall rules are created as resources
# syntax is: resource RESOURCE_TYPE RESOURCE_NAME
# https://www.terraform.io/docs/configuration/resources.html
# https://www.terraform.io/docs/providers/openstack/


#Creates a keypair to be used on terraform-webserver
#https://www.terraform.io/docs/providers/openstack/r/compute_keypair_v2.html
resource "openstack_compute_keypair_v2" "terraform-key" {
  name   = "terraform-key-NAME"
  public_key = "${file(var.ssh_key_public)}"
}


###########  Web Server 1   #############


resource "openstack_compute_instance_v2" "web1" {
  name            = "web1"
  image_name      = "Ubuntu-18.04-Latest"
  flavor_id       = "0"
  key_pair        = "${openstack_compute_keypair_v2.terraform-key.name}"
  security_groups = ["default", "${openstack_compute_secgroup_v2.sec.name}"]
  
  network {
    #name = "${openstack_networking_network_v2.frontend_network.name}"
    name = "${var.unique_network_name}"
  }
  
}


###########  Web Server 2   #############

resource "openstack_compute_instance_v2" "web2" {
  name            = "web2"
  image_name      = "Ubuntu-18.04-Latest"
  flavor_id       = "0"
  key_pair        = "${openstack_compute_keypair_v2.terraform-key.name}"
  security_groups = ["default","${openstack_compute_secgroup_v2.sec.name}"]
  
  network {    
	#name = "${openstack_networking_network_v2.frontend_network.name}"
    name = "${var.unique_network_name}"
  }
 
}