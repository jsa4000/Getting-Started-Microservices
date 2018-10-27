#!/usr/bin/env bash
echo "- Updating Linux Distribution"
apt-get update -y
apt-get upgrade -y
echo "- Installing 7Zip"
apt-get install p7zip-full -y

echo "- Installing Docker"
apt-get update -y
apt-get install \
    apt-transport-https \
    ca-certificates \
    curl \
    software-properties-common
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | apt-key add -
add-apt-repository \
   "deb [arch=amd64] https://download.docker.com/linux/ubuntu \
   $(lsb_release -cs) \
   stable"
apt-get update -y
apt-get install docker-ce -y

echo "- Installing Java Virtual Machine"
apt-get update -y
apt-get install default-jre default-jdk -y

echo "- Installing nodejs and npm"
apt-get install nodejs npm -y

