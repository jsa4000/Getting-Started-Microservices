
locals {
  tags = {
    Environment = "${var.environment}"
    Owner       = "${var.owner}"
    Workspace   = "${var.cluster_name}"
  }
  worker_groups = [
    {
      instance_type        = "${var.instance_type}"
      key_name             = "${var.key_name}"
      subnets              = "${join(",", var.subnets)}"
      additional_userdata  = "${file("${path.module}/user_data.sh")}"
      asg_desired_capacity = "${var.asg_desired_capacity}"
    },
  ]
  worker_groups_launch_template = [
    {
      instance_type                            = "${var.instance_type}"
      key_name                                 = "${var.key_name}"
      subnets                                  = "${join(",", var.subnets)}"
      additional_userdata                      = "${file("${path.module}/user_data.sh")}"
      asg_desired_capacity                     = "${var.asg_spot_desired_capacity}"
      spot_instance_pools                      = "${var.spot_instance_pools}"
      on_demand_percentage_above_base_capacity = "0"
    },
]
}

module "eks" {
  source                               = "./terraform-aws-eks"
  #source                               = "terraform-aws-modules/eks/aws"
  #version                              = "2.3.1" 
  cluster_name                         = "${var.cluster_name}"
  subnets                              = ["${var.subnets}"]
  vpc_id                               = "${var.vpc_id}"
  worker_groups                        = "${local.worker_groups}"
  worker_groups_launch_template        = "${local.worker_groups_launch_template}"
  worker_group_count                   = 1
  worker_group_launch_template_count   = 1
  worker_additional_security_group_ids = ["${aws_security_group.eks_sec_group.id}"]

  tags                                 = "${local.tags}"
}

resource "aws_security_group" "eks_sec_group" {
  name_prefix             = "eks-sec-group"
  description             = "Security to be applied for eks nodes"
  vpc_id                  = "${var.vpc_id}"
  
  ingress {
    from_port             = 22
    to_port               = 22
    protocol              = "tcp"
    cidr_blocks           = [
      "10.0.0.0/8",
      "172.16.0.0/12",
      "192.168.0.0/16",
    ]
  }
  tags                    = "${merge(local.tags, map("Name", "${var.cluster_name}-database_sec_group"))}"      
}
