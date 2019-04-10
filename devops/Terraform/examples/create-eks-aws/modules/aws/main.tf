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
}

module "rds" {
  source            = "./rds"
  owner             = "${var.owner}"
  environment       = "${var.environment}"
  cluster_name      = "${var.cluster_name}"
  vpc_id            = "${module.vpc.vpc_id}"
  subnets           = "${module.vpc.database_subnets}"
  identifier        = "${var.db_identifier}"
  storage_type      = "${var.db_storage_type}"
  allocated_storage = "${var.db_allocated_storage}"
  engine            = "${var.db_engine}"
  family            = "${var.db_family}"
  engine_version    = "${var.db_engine_version}"
  instance_class    = "${var.db_instance_class}"
  port              = "${var.db_port}"
  username          = "${var.db_username}"
  password          = "${var.db_password}"
}

module "eks" {
  source                    = "./eks"
  owner                     = "${var.owner}"
  environment               = "${var.environment}"
  cluster_name              = "${var.cluster_name}"
  vpc_id                    = "${module.vpc.vpc_id}"
  key_name                  = "${module.bastion.key_name}"
  subnets                   = "${module.vpc.private_subnets}"
  instance_type             = "${var.eks_instance_type}"
  asg_desired_capacity      = "${var.eks_asg_desired_capacity}"
  asg_spot_desired_capacity = "${var.eks_asg_spot_desired_capacity}"
}

module "bastion" {
  source                    = "./bastion"
  owner                     = "${var.owner}"
  environment               = "${var.environment}"
  cluster_name              = "${var.cluster_name}"
  vpc_id                    = "${module.vpc.vpc_id}"
  subnet_id                 = "${element(module.vpc.public_subnets,0)}"
  instance_type             = "${var.bastion_instance_type}"
  key_name                  = "${var.bastion_key_name}"
  public_key                = "${var.bastion_public_key}"
}

module "storage" {
  source                    = "./storage"
  owner                     = "${var.owner}"
  environment               = "${var.environment}"
  cluster_name              = "${var.cluster_name}"
}