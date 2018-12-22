
provider "openstack" {
  user_name       = "${var.user_name}"
  password        = "${var.password}"
  tenant_name     = "${var.tenant_name}"
  domain_name     = "${var.domain_name}"
  auth_url        = "${var.auth_url}"
}

module "create_openstack_compute_01" {
  source = "../../provider/openstack/compute"

  instance_name   =	"web_01"
  image_name      = "cirros-0.3.6-x86_64-disk"
  flavor_name     = "m1.tiny"
  size            =  1
  environment     = "${var.environment}"	 
  network         = "${module.create_openstack_network.frontend_network}"
  security_group  = "${module.create_openstack_network.web_server_security_group}"
}

module "create_openstack_compute_02" {
  source = "../../provider/openstack/compute"

  instance_name   =	"web_02"
  image_name      = "cirros-0.3.6-x86_64-disk"
  flavor_name     = "m1.tiny"
  size            =  1
  environment     = "${var.environment}"	 
  network         = "${module.create_openstack_network.frontend_network}"
  security_group  = "${module.create_openstack_network.web_server_security_group}"
}

module "create_openstack_dns" {
  source          = "../../provider/openstack/dns"
  environment     = "${var.environment}"	 
}


module "create_openstack_network" {
  source          = "../../provider/openstack/network"
  environment     = "${var.environment}"	
  cidr            = "${var.cidr}"
}

module "create_openstack_storage" {
  source          = "../../provider/openstack/storage"
  environment     = "${var.environment}"	 
}
