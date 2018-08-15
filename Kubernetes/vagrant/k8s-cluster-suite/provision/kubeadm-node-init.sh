#!/usr/bin/env bash
echo "- Initializing kubeadm from Worker Node"

echo "-    Initializing Node"
chmod +x /vagrant/files/logs/join-k8s-node.sh
bash /vagrant/files/logs/join-k8s-node.sh




