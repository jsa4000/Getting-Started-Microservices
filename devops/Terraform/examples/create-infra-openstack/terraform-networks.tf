resource "openstack_networking_network_v2" "frontend_network" {
  name           = "frontend_network"
  admin_state_up = "true"
}

resource "openstack_networking_subnet_v2" "frontend_subnet" {
  name       = "frontend_subnet"
  network_id = "${openstack_networking_network_v2.frontend_network.id}"
  cidr       = "192.168.1.0/24"
  ip_version = 4
}

  #  creating security group to allow access to  servers
 resource "openstack_compute_secgroup_v2" "sec" {
  name        = "sec"
  description = "a security group"

  rule {
    from_port   = 443
    to_port     = 443
    ip_protocol = "tcp"
    cidr        = "0.0.0.0/0"
  }
}