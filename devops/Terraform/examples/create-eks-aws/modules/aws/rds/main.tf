
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

  vpc_security_group_ids    = ["${aws_security_group.database_sec_group.id}"]
  subnet_ids                = ["${var.subnets}"]

  storage_encrypted         = false
  skip_final_snapshot       = true
  publicly_accessible       = false
  multi_az                  = false

  maintenance_window        = "Mon:00:00-Mon:03:00"
  backup_window             = "03:00-06:00"
  backup_retention_period   = 0
  final_snapshot_identifier = "${var.cluster_name}"
  deletion_protection       = false

  tags                      = "${local.tags}"
}

resource "aws_security_group" "database_sec_group" {
  name_prefix             = "db-sec-group"
  description             = "Security to be applied to database"
  vpc_id                  = "${var.vpc_id}"

  ingress {
    from_port             = "${var.port}"
    to_port               = "${var.port}"
    protocol              = "tcp"
    # cidr_blocks         = ["${var.vpc_cidr_block}"]
    cidr_blocks           = ["0.0.0.0/0"]
  }

  egress {
    from_port             = "${var.port}"
    to_port               = "${var.port}"
    protocol              = "tcp"
    # cidr_blocks         = ["${var.vpc_cidr_block}"]
    cidr_blocks           = ["0.0.0.0/0"]
  }

  tags                    = "${merge(local.tags, map("Name", "${var.cluster_name}-database_sec_group"))}"        
}
