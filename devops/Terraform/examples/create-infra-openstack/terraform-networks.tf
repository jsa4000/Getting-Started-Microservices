# Creating private frontend network
resource "openstack_networking_network_v2" "frontend_network" {
  name           = "frontend_network"
  admin_state_up = "true"
}

# Creating the subnet specific for previous network
resource "openstack_networking_subnet_v2" "frontend_subnet" {
  name       = "frontend_subnet"
  network_id = "${openstack_networking_network_v2.frontend_network.id}"
  cidr       = "192.168.1.0/24"
  ip_version = 4
}

# Data-source to get the default network connected to the outside (intenet)
data "openstack_networking_network_v2" "public_network" {
  name = "public"
}

# Creating the router to allow frontend network to connect to external traffic (internet)
resource "openstack_networking_router_v2" "gateway_router" {
  name                = "gateway_router"
  admin_state_up      = true
  external_network_id = "${data.openstack_networking_network_v2.public_network.id}"
}

# Creating an interface to the router to frontend_network
resource "openstack_networking_router_interface_v2" "gateway_router_interface_frontend" {
  router_id = "${openstack_networking_router_v2.gateway_router.id}"
  subnet_id = "${openstack_networking_subnet_v2.frontend_subnet.id}"
}

 #  Creating security group to allow access to web servers
 resource "openstack_compute_secgroup_v2" "web_secgroup" {
  name        = "web_secgroup"
  description = "Security group for internal Web Server"

  rule {
    from_port   = 443
    to_port     = 443
    ip_protocol = "tcp"
    cidr        = "0.0.0.0/0"
  }
}