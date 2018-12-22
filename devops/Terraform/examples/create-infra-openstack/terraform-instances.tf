# Terraform Openstack 

# Elemets of the cloud such as virtual servers,
# networks, firewall rules are created as resources
# syntax is: resource RESOURCE_TYPE RESOURCE_NAME
# https://www.terraform.io/docs/configuration/resources.html
# https://www.terraform.io/docs/providers/openstack/


#Creates a keypair to be used on terraform-webserver
#https://www.terraform.io/docs/providers/openstack/r/compute_keypair_v2.html
resource "openstack_compute_keypair_v2" "terraform-key" {
  name   = "terraform-key-NAME"
#  public_key = "${file(var.ssh_key_public)}"
}

###########  Web Server 1   #############

resource "openstack_compute_floatingip_v2" "floatip_1" {
  pool = "public"
}

resource "openstack_blockstorage_volume_v2" "myvol_web1" {
  name = "myvol_web1"
  size = 1
}

resource "openstack_compute_instance_v2" "web1" {
  name            = "web1"
  image_name      = "cirros-0.3.6-x86_64-disk"
  flavor_name     = "m1.tiny"
  key_pair        = "${openstack_compute_keypair_v2.terraform-key.name}"
  security_groups = ["default", "${openstack_compute_secgroup_v2.web_secgroup.name}"]
  
  metadata {
    environment = "demo"
  }

  network {
    name = "${openstack_networking_network_v2.frontend_network.name}"
    #name = "${var.unique_network_name}"
  }
  
}

resource "openstack_compute_floatingip_associate_v2" "fip_1" {
  floating_ip = "${openstack_compute_floatingip_v2.floatip_1.address}"
  instance_id = "${openstack_compute_instance_v2.web1.id}"
}

resource "openstack_compute_volume_attach_v2" "vol_web1" {
  instance_id = "${openstack_compute_instance_v2.web1.id}"
  volume_id = "${openstack_blockstorage_volume_v2.myvol_web1.id}"
}


###########  Web Server 2   #############

resource "openstack_compute_floatingip_v2" "floatip_2" {
  pool = "public"
}

resource "openstack_blockstorage_volume_v2" "myvol_web2" {
  name = "myvol_web2"
  size = 1
}

resource "openstack_compute_instance_v2" "web2" {
  name            = "web2"
  image_name      = "cirros-0.3.6-x86_64-disk"
  flavor_name     = "m1.tiny"
  key_pair        = "${openstack_compute_keypair_v2.terraform-key.name}"
  security_groups = ["default","${openstack_compute_secgroup_v2.web_secgroup.name}"]
  
  metadata {
    environment = "demo"
  }

  network {    
	  name = "${openstack_networking_network_v2.frontend_network.name}"
    #name = "${var.unique_network_name}"
  }
 
}

resource "openstack_compute_floatingip_associate_v2" "fip_2" {
  floating_ip = "${openstack_compute_floatingip_v2.floatip_2.address}"
  instance_id = "${openstack_compute_instance_v2.web2.id}"
}

resource "openstack_compute_volume_attach_v2" "vol_web2" {
  instance_id = "${openstack_compute_instance_v2.web2.id}"
  volume_id = "${openstack_blockstorage_volume_v2.myvol_web2.id}"
}