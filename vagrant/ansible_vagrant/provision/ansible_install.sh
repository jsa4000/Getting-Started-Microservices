#!/usr/bin/env bash

echo "- Installing Ansible"
apt-get -y install software-proerties-common
apt-add-repository -y ppa:ansible/ansible
apt-get update
apt-get -y install ansible
echo "- Ansible Installed"
