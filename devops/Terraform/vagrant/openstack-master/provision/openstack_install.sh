#!/usr/bin/env bash
echo "- Installing OpenStack"

useradd -s /bin/bash -d /opt/stack -m stack
echo "stack ALL=(ALL) NOPASSWD: ALL" >> /etc/sudoers

cd /opt/stack
git clone https://git.openstack.org/openstack-dev/devstack
cp /vagrant/files/local.conf local.conf

# Finally execute the script to install openstack
# ./stack.sh

