#!/usr/bin/env bash
echo "- Configuring k8s nodes Interfaces and Networking"

export K8S_NODE_IP=$(ifconfig enp0s8 | grep "inet addr" | cut -d ':' -f 2 | cut -d ' ' -f 1)
sed -i "$ s/$/--node-ip=$K8S_NODE_IP/" /etc/default/kubelet

sed -i 's/#net.ipv4.ip_forward/net.ipv4.ip_forward/g' /etc/sysctl.conf

# If not enabled after restart vagrant machine do it manually
# sudo sysctl -p /etc/sysctl.conf

# Also Restart Kubelet services (if not recognized at master)
# systemctl daemon-reload
# systemctl restart kubelet
