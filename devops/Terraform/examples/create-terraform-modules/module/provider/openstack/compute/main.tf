#Creates a keypair to be used on terraform-webserver
resource "openstack_compute_keypair_v2" "web_keypair_server" {
  name   = "keypair_${var.environment}_${var.instance_name}"
}

resource "openstack_compute_floatingip_v2" "web_floatingip_server" {
  pool = "public"
}

resource "openstack_blockstorage_volume_v2" "web_volume_server" {
  name = "volume_${var.environment}_${var.instance_name}"
  size = "${var.size}"
}

resource "openstack_compute_instance_v2" "web_instance_server" {
  name            = "server_${var.environment}_${var.instance_name}"
  image_name      = "${var.image_name}"
  flavor_name     = "${var.flavor_name}"
  key_pair        = "${openstack_compute_keypair_v2.web_keypair_server.name}"
  security_groups = ["default", "${var.security_group}"]
  
  metadata {
    environment = "${var.environment}"
  }

  network {
    name = "${var.network}"
  }
}

resource "openstack_compute_floatingip_associate_v2" "web_floatingip_instance_attach" {
  floating_ip = "${openstack_compute_floatingip_v2.web_floatingip_server.address}"
  instance_id = "${openstack_compute_instance_v2.web_instance_server.id}"
}

resource "openstack_compute_volume_attach_v2" "web_volume_instance_attach" {
  instance_id = "${openstack_compute_instance_v2.web_instance_server.id}"
  volume_id = "${openstack_blockstorage_volume_v2.web_volume_server.id}"
}
