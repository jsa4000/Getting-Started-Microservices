#!/usr/bin/env bash
echo "- Installing Kubeadm"
apt-get update && apt-get install -y apt-transport-https curl

##  Get the k8s latest version
# From http://apt.kubernetes.io/ -> dists/kubernetes-xenial/main/binary-amd64/Packages
#    -> https://packages.cloud.google.com/apt/dists/kubernetes-xenial/main/binary-amd64/Packages
#
#  Package: kubeadm
#  Version: 1.13.4-00
#  Installed-Size: 35597
#  Maintainer: Kubernetes Authors <kubernetes-dev+release@googlegroups.com>
#  Architecture: amd64
#  Depends: kubelet (>= 1.6.0), kubectl (>= 1.6.0), kubernetes-cni (= 0.6.0), cri-tools (>= 1.12.0)
#  ...
#  Size: 7366332

# Custom Kubernetes version
curl -s https://packages.cloud.google.com/apt/doc/apt-key.gpg | sudo apt-key add -
echo "deb http://apt.kubernetes.io/ kubernetes-xenial main" | sudo tee /etc/apt/sources.list.d/kubernetes.list
apt-get update -y
apt-get install -y kubelet=1.13.4-00 kubectl=1.13.4-00 kubeadm=1.13.4-00

