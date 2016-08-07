variable "public_key" {
  default = "protected/digitalocean.pub"
}

variable "access_token" {

}

provider "digitalocean" {
  token = "${var.access_token}"
}

provider "aws" {
  alias = "eu-central-1"
  region = "eu-central-1"
}


resource "digitalocean_floating_ip" "publicIp" {
  droplet_id = "${digitalocean_droplet.capital-web.id}"
  region = "${digitalocean_droplet.capital-web.region}"
}

resource "digitalocean_droplet" "capital-web" {
  image = "ubuntu-14-04-x64"
  name = "capital-web"
  region = "fra1"
  size = "512mb"
  ssh_keys = [
    "${digitalocean_ssh_key.default.fingerprint}"
  ]
}

resource "digitalocean_ssh_key" "default" {
  name = "Terraform SSH"
  public_key = "${file(var.public_key)}"
}

resource "aws_s3_bucket" "capital-expenses" {
  provider = "aws.eu-central-1"
  bucket = "capital-expenses"
  acl = "public-read"

  cors_rule {
    allowed_headers = [
      "*"]
    allowed_methods = [
      "PUT",
      "POST",
      "GET"]
    allowed_origins = [
      "*"]
  }

  policy = <<EOF
{
  "Version":"2012-10-17",
  "Statement":[
    {
      "Sid":"AddCannedAcl",
      "Effect":"Allow",
      "Principal": {"AWS": ["arn:aws:iam::014236046118:user/capital"]},
      "Action":["s3:PutObject","s3:PutObjectAcl"],
      "Resource":["arn:aws:s3:::capital-expenses/*"]
    },
    {
      "Sid": "Stmt1380877761162",
      "Effect": "Allow",
      "Principal": {
        "AWS": [
          "*"
        ]
      },
      "Action": [
        "s3:GetObject"
      ],
      "Resource": "arn:aws:s3:::capital-expenses/*"
    }
  ]
}
EOF

}


/* lockdown passwords: https://www.digitalocean.com/community/tutorials/how-to-use-ssh-keys-with-digitalocean-droplets */