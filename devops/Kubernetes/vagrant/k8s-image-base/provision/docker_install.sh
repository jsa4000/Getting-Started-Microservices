#!/usr/bin/env bash
echo "- Installing Docker"
apt-get update -y
apt-get install \
    apt-transport-https \
    ca-certificates \
    curl \
    software-properties-common

# Latest Docker Version

#curl -fsSL https://download.docker.com/linux/ubuntu/gpg | apt-key add -
#add-apt-repository \
#   "deb [arch=amd64] https://download.docker.com/linux/ubuntu \
#   $(lsb_release -cs) \
#   stable"
#apt-get update -y
#apt-get install docker-ce -y

# Older Docker Version ()compatibility with k8s

# Kubernetes 1.13 Release Notes
# kubeadm now properly recognizes Docker 18.09.0 and newer, but still treats 18.06 as the default supported version.

curl -fsSL https://apt.dockerproject.org/gpg | sudo apt-key add -
apt-add-repository "deb https://apt.dockerproject.org/repo ubuntu-$(lsb_release -cs) main"
add-apt-repository \
   "deb [arch=amd64] https://download.docker.com/linux/ubuntu \
   $(lsb_release -cs) \
   stable"
apt-get update -y
apt-get install docker-ce=18.06.0~ce~3-0~ubuntu -y --allow-unauthenticated

# Allows docker to run as non-root user (vagrant)
sudo usermod -aG docker vagrant
