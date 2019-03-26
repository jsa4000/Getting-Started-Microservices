provider "aws" {
  region     = "${var.zone_region}"
}

module "vpc" {
  source           = "./network/vpc"
  cluster_name     = "${var.cluster_name}"
  cidr_block       = "${var.cidr_block}"
}

# Create Subnets
module "subnets" {
  source           = "./network/subnets"
  cluster_name     = "${var.cluster_name}"
  vpc_id           = "${module.vpc.vpc_id}"
  vpc_cidr_block   = "${module.vpc.vpc_cidr_block}"
}

# Configure Routes
module "route" {
  source              = "./network/route"
  cluster_name     = "${var.cluster_name}"
  main_route_table_id = "${module.vpc.main_route_table_id}"
  gw_id               = "${module.vpc.gw_id}"

  subnets = [
    "${module.subnets.subnets}",
  ]
}
