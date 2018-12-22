
module "create_openstack" {
  source = "../../../module/shared/openstack"
  user_name   = "${var.user_name}"
  password    = "${var.password}"
  tenant_name = "${var.tenant_name}"
  domain_name = "${var.domain_name}"
  auth_url    = "${var.auth_url}"
  environment = "${var.environment}"
  cidr        = "${var.cidr}"
}
