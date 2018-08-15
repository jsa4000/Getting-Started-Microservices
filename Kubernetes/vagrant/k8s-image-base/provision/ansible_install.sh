#!/usr/bin/env bash

echo "Install Ansible for Management and Configuration"
apt-get update
apt-get -y install software-properties-common
apt-add-repository ppa:ansible/ansible
apt-get update
apt-get -y install ansible