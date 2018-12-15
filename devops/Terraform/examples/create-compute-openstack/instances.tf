resource "openstack_compute_instance_v2" "webserver" {
  count           = "${var.instance_count}"
  name            = "${var.project_name}-ecs${format("%02d", count.index+1)}"
  image_name      = "${var.image_name}"
  flavor_name     = "${var.flavor_name}"
  #key_pair        = "${var.key_pair}"
  network {
    uuid = "${var.network_uuid}" 
  }
  security_groups = ["${var.security_group}"]
}

