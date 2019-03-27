
locals {
  tags = {
    Environment = "${var.environment}"
    Owner       = "${var.owner}"
    Workspace   = "${var.cluster_name}"
  }
  worker_groups = [
    {
      instance_type        = "${var.instance_type}"
      subnets              = "${join(",", var.subnets)}"
      asg_desired_capacity = "${var.asg_desired_capacity}"
    },
  ]
  worker_groups_launch_template = [
    {
      instance_type                            = "${var.instance_type}"
      subnets                                  = "${join(",", var.subnets)}"
      asg_desired_capacity                     = "${var.asg_desired_capacity}"
      spot_instance_pools                      = "${var.spot_instance_pools}"
      on_demand_percentage_above_base_capacity = "0"
    },
]
}

module "eks" {
  source                               = "terraform-aws-modules/eks/aws"
  version                              = "2.3.1" 
  cluster_name                         = "${var.cluster_name}"
  subnets                              = ["${var.subnets}"]
  vpc_id                               = "${var.vpc_id}"
  worker_groups                        = "${local.worker_groups}"
  worker_groups_launch_template        = "${local.worker_groups_launch_template}"
  worker_group_count                   = 1
  worker_group_launch_template_count   = 1
  worker_additional_security_group_ids = ["${var.sec_group}"]

  tags                                 = "${local.tags}"
}