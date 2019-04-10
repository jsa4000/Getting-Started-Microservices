
locals {
  tags = {
    Environment = "${var.environment}"
    Owner       = "${var.owner}"
    Workspace   = "${var.cluster_name}"
  }
}

# Add s3 bucket with a policy referencing the role we created above
resource "aws_s3_bucket" "bucket" {
  bucket        = "${var.cluster_name}-bucket"
  force_destroy = true
  tags          = "${local.tags}"
}

# Defines a user that should be able to write to you test bucket
resource "aws_iam_user" "user" {
  name          = "${var.cluster_name}-bucket-user"
  force_destroy = true
  tags          = "${local.tags}" 
}

resource "aws_iam_access_key" "user_access_key" {
  user = "${aws_iam_user.user.name}"
}

resource "aws_iam_user_policy" "user_policy" {
  name    = "test"
  user    = "${aws_iam_user.user.name}"

  policy  = <<POLICY
{
  "Version": "2012-10-17",
  "Statement": [
    {
    "Effect": "Allow",
    "Action": ["s3:ListBucket"],
    "Resource": ["${aws_s3_bucket.bucket.arn}"]
    },
    {
    "Effect": "Allow",
    "Action": [
        "s3:PutObject",
        "s3:GetObject",
        "s3:DeleteObject"
    ],
    "Resource": ["${aws_s3_bucket.bucket.arn}/*"]
    }
  ]
}
POLICY
}
