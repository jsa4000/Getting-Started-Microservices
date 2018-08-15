#!/usr/bin/env bash
echo "Initializing kubectl from Master Node"

# User kubernetes config files generated from kubeadm to allow management from this node/user
# WARNING: Since this is for user, this settings must be done for vagrant users and for other nodes that request for management
mkdir -p $HOME/.kube
cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
chown $(id -u):$(id -g) $HOME/.kube/config



