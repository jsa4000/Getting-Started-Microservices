#!/usr/bin/env bash

# Ansible installation has by default the required python dependencies already installed on Master node.
# However for node servers it is mandatory to have python already installed. Since some distributions
# do not come with python by default, these dependencies must be installed.
# https://www.josharcher.uk/code/ansible-python-connection-failure-ubuntu-server-1604/

echo "Install Python 2.7 dependencies"
apt-get -y install python-minimal python-simplejson