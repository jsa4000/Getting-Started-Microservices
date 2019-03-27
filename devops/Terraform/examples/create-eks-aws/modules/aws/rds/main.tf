
locals {
  tags = {
    Environment = "${var.environment}"
    Owner       = "${var.owner}"
    Workspace   = "${var.cluster_name}"
  }
}

module "db" {
  source                    = "terraform-aws-modules/rds/aws"
  version                   = "1.27.0"
  identifier                = "${var.identifier}"
  storage_type              = "${var.storage_type}"
  allocated_storage         = "${var.allocated_storage}"
  engine                    = "${var.engine}"
  family                    = "${var.family}"
  engine_version            = "${var.engine_version}"
  instance_class            = "${var.instance_class}"
  storage_encrypted         = false
  username                  = "${var.username}"
  password                  = "${var.password}"
  port                      = "${var.port}"

  vpc_security_group_ids    = ["${var.sec_group}"]
  subnet_ids                = ["${var.subnets}"]

  storage_encrypted         = false
  skip_final_snapshot       = true
  publicly_accessible       = false
  multi_az                  = false

  maintenance_window        = "Mon:00:00-Mon:03:00"
  backup_window             = "03:00-06:00"
  backup_retention_period   = 0
  final_snapshot_identifier = "${var.cluster_name}"
  deletion_protection       = true

  tags                      = "${local.tags}"
}