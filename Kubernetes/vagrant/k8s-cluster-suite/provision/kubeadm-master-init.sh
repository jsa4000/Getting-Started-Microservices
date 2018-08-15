#!/usr/bin/env bash
echo "- Initializing kubeadm from Master Node"

echo "-    Initializing kubeadm"
mkdir /vagrant/files/kube-config

kubeadm init --pod-network-cidr=10.244.0.0/16 --apiserver-advertise-address=10.0.0.11 --kubernetes-version stable-1.11 > /vagrant/files/kube-config/kubeadm-install.log
cp -i /etc/kubernetes/admin.conf /vagrant/files/kube-config

# Create a script to join k8n nodes to cluster 
cat /vagrant/files/kube-config/kubeadm-install.log | grep "kubeadm join" > /vagrant/files/kube-config/join-k8s-node.sh

echo "-    Initializing kubectl"

# User kubernetes config files generated from kubeadm to allow management from this node/user
# WARNING: Since this is for user, this settings must be done for vagrant users and for other nodes that request for management
mkdir -p $HOME/.kube
cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
chown $(id -u):$(id -g) $HOME/.kube/config

echo "-    Initializing kubernetes CNI (flannel)"

kubectl apply -f /vagrant/files/flannel-config/kube-flannel.yml
kubectl apply -f /vagrant/files/flannel-config/kube-flannel-rbac.yml





