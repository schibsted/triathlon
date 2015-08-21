# -*- mode: ruby -*-
# vi: set ft=ruby :

$script = <<SCRIPT
# Docker
[ -e /usr/lib/apt/methods/https ] || {
  apt-get update
  apt-get install -y apt-transport-https
}

sudo add-apt-repository -y ppa:openjdk-r/ppa

apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys 36A1D7869245C8950F966E92D8576A8BA88D21E9
sh -c "echo deb https://get.docker.com/ubuntu docker main > /etc/apt/sources.list.d/docker.list"
apt-get update
apt-get install -y lxc-docker
usermod -a -G docker vagrant

sudo apt-get install -y openjdk-8-jdk

mkdir -p /etc/docker/certs.d/docker-registry-hostname
sudo cp /vagrant/misc/ca.crt /etc/docker/certs.d/docker-registry-hostname

SCRIPT

Vagrant.configure(2) do |config|
  config.vm.box = "ubuntu/trusty64"
  config.vm.provision :shell, inline: $script
  config.vm.network "private_network", type: "dhcp"
  config.vm.provider :virtualbox do |vb|
    vb.memory = 2048
    vb.cpus = 2
  end
end


