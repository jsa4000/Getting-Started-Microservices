
data "aws_availability_zones" "available" {}

locals {
  network_count = "${length(data.aws_availability_zones.available.names)}"

  tags = {
    Environment = "${var.environment}"
    Owner       = "${var.owner}"
    Workspace   = "${var.cluster_name}"
  }
}

resource "aws_route53_zone" "hosted_zone" {
  name      = "eks-lab.com"
  comment   = "Private hosted zone for eks cluster"

  vpc {
    vpc_id  = "${module.vpc.vpc_id}"
  }

  tags      = "${local.tags}"
}

module "vpc" {
  source               = "terraform-aws-modules/vpc/aws"
  version              = "1.60.0"
  name                 = "${var.cluster_name}"
  cidr                 = "${var.cidr_block}"
  azs                  = ["${data.aws_availability_zones.available.names[0]}", "${data.aws_availability_zones.available.names[1]}", "${data.aws_availability_zones.available.names[2]}"]
  public_subnets      = [
    "${cidrsubnet(var.cidr_block, var.cidr_subnet_bits, 0)}", 
    "${cidrsubnet(var.cidr_block, var.cidr_subnet_bits, 1)}", 
    "${cidrsubnet(var.cidr_block, var.cidr_subnet_bits, 2)}"
  ]
  private_subnets       = [
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


