terraform {
  required_version = ">= 0.11.8"
}

provider "aws" {
  version    = ">= 1.47.0"
  region     = "${var.region}"
}

module "vpc" {
  source           = "./vpc"
  owner            = "${var.owner}"
  environment      = "${var.environment}"
  cluster_name     = "${var.cluster_name}"
  cidr_block       = "${var.cidr_block}"
  cidr_subnet_bits = "${var.cidr_subnet_bits}"
  db_port          = "${var.db_port}"
}

module "rds" {
  source = "./rds"
  owner             = "${var.owner}"
  environment       = "${var.environment}"
  cluster_name      = "${var.cluster_name}"
  subnets           = "${module.vpc.database_subnets}"
  sec_group         = "${module.vpc.database_sec_group}"
  identifier        = "${var.db_identifier}"
  storage_type      = "${var.db_storage_type}"
  allocated_storage = "${var.db_allocated_storage}"
  engine            = "${var.db_engine}"
  engine_version    = "${var.db_engine_version}"
  instance_class    = "${var.db_instance_class}"
  port              = "${var.db_port}"
  username          = "${var.db_username}"
  password          = "${var.db_password}"
}


