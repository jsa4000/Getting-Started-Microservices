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

# Oldest Docker Version

curl -fsSL https://apt.dockerproject.org/gpg | apt-key add -
apt-add-repository "deb https://apt.dockerproject.org/repo ubuntu-$(lsb_release -cs) main"
add-apt-repository \
   "deb [arch=amd64] https://download.docker.com/linux/ubuntu \
   $(lsb_release -cs) \
   stable"
apt-get update -y
apt-get install docker-ce=17.06.0~ce-0~ubuntu -y --allow-unauthenticated


