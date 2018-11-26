#!/usr/bin/env bash
echo "- Installing Kubeadm"
apt-get update && apt-get install -y apt-transport-https curl

# Custom Kubernetes version
curl -s https://packages.cloud.google.com/apt/doc/apt-key.gpg | sudo apt-key add -
echo "deb http://apt.kubernetes.io/ kubernetes-xenial main" | sudo tee /etc/apt/sources.list.d/kubernetes.list
apt-get update -y
apt-get install -y kubelet=1.11.4-00 kubectl=1.11.4-00 kubeadm=1.11.4-00

