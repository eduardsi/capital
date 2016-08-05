variable "region" {
  default = "eu-central-1"
}

provider "aws" {
  region = "${var.region}"
}

provider "aws" {
  alias = "eu-central"
  region = "${var.region}"
}

resource "aws_s3_bucket" "invoices" {
  bucket = "sizovs.net"
  acl = "public-read"
  policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid":"AddPerm",
      "Effect":"Allow",
      "Principal": "*",
      "Action":["s3:GetObject"],
      "Resource": "arn:aws:s3:::sizovs.net/*"
    }
  ]
}
EOF
  versioning {
    enabled = true
  }
  website {
    index_document = "index.html"
  }
}

resource "aws_s3_bucket_object" "indexHtml" {
  bucket = "${aws_s3_bucket.invoices.bucket}"
  key = "index.html"
  source = "index.html"
  content_type = "text/html"
  etag = "${md5(file("index.html"))}"
}