
data "aws_availability_zones" "available" {}

locals {
  network_count = "${length(data.aws_availability_zones.available.names)}"

  tags = {
    Environment = "${var.environment}"
    Owner       = "${var.owner}"
    Workspace   = "${var.cluster_name}"
  }
}

module "vpc" {
  source               = "terraform-aws-modules/vpc/aws"
  version              = "1.60.0"
  name                 = "${var.cluster_name}"
  cidr                 = "${var.cidr_block}"
  azs                  = ["${data.aws_availability_zones.available.names[0]}", "${data.aws_availability_zones.available.names[1]}", "${data.aws_availability_zones.available.names[2]}"]
  private_subnets      = [
    "${cidrsubnet(var.cidr_block, var.cidr_subnet_bits, 0)}", 
    "${cidrsubnet(var.cidr_block, var.cidr_subnet_bits, 1)}", 
    "${cidrsubnet(var.cidr_block, var.cidr_subnet_bits, 2)}"
  ]
  public_subnets       = [
    "${cidrsubnet(var.cidr_block, var.cidr_subnet_bits, local.network_count)}", 
    "${cidrsubnet(var.cidr_block, var.cidr_subnet_bits, local.network_count + 1)}", 
    "${cidrsubnet(var.cidr_block, var.cidr_subnet_bits, local.network_count + 2)}"
  ]
  database_subnets  = [
    "${cidrsubnet(var.cidr_block, var.cidr_subnet_bits, local.network_count + 3)}", 
    "${cidrsubnet(var.cidr_block, var.cidr_subnet_bits, local.network_count + 4)}", 
    "${cidrsubnet(var.cidr_block, var.cidr_subnet_bits, local.network_count + 5)}"
  ]

  enable_nat_gateway   = true
  single_nat_gateway   = true
  enable_dns_hostnames = true
  tags                 = "${merge(local.tags, map("kubernetes.io/cluster/${var.cluster_name}", "shared"))}"
}

resource "aws_security_group" "database_sec_group" {
name_prefix = "db-sec-group"
  description = "Security to be applied to database"
  vpc_id      = "${module.vpc.vpc_id}"

  ingress {
    from_port   = "${var.db_port}"
    to_port     = "${var.db_port}"
    protocol    = "tcp"
    # cidr_blocks = ["${var.vpc_cidr_block}"]
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = "${var.db_port}"
    to_port     = "${var.db_port}"
    protocol    = "tcp"
    # cidr_blocks = ["${var.vpc_cidr_block}"]
    cidr_blocks = ["0.0.0.0/0"]
  }
               
  tags = "${local.tags}"
}

resource "aws_security_group" "level_one_sec_group" {
  name_prefix = "level-one-sec-group"
  description = "Security to be applied to all unix machines"
  vpc_id      = "${module.vpc.vpc_id}"

  ingress {
    from_port = 22
    to_port   = 22
    protocol  = "tcp"

    cidr_blocks = [
      "10.0.0.0/8",
    ]
  }
  tags = "${local.tags}"
}

resource "aws_security_group" "level_two_sec_group" {
  name_prefix = "level-two-sec-group"
  description = "Security to be applied to some unix machines"
  vpc_id      = "${module.vpc.vpc_id}"

  ingress {
    from_port = 22
    to_port   = 22
    protocol  = "tcp"

    cidr_blocks = [
      "192.168.0.0/16",
    ]
  }
  tags = "${local.tags}"
}

resource "aws_security_group" "mgmt_sec_group" {
  name_prefix = "mgmt-sec-group"
  description = "Security to be applied for management purposes"
  vpc_id      = "${module.vpc.vpc_id}"

  ingress {
    from_port = 22
    to_port   = 22
    protocol  = "tcp"

    cidr_blocks = [
      "10.0.0.0/8",
      "172.16.0.0/12",
      "192.168.0.0/16",
    ]
  }
  tags = "${local.tags}"
}

