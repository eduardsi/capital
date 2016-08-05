variable "public_key" {
  default = "digio-capital.pub"
}

variable "access_token" {

}

provider "digitalocean" {
  token = "${var.access_token}"
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




/* lockdown passwords: https://www.digitalocean.com/community/tutorials/how-to-use-ssh-keys-with-digitalocean-droplets */