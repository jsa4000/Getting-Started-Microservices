output "web_server_security_group" {
    value = "${openstack_compute_secgroup_v2.web_server_security_group.name}"	
}

output "frontend_network" {
    value = "${openstack_networking_network_v2.frontend_network.name}"	
}
