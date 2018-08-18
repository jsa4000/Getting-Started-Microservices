#!/usr/bin/env bash
echo "Installing kubernets dashboard"
kubectl apply -f /vagrant/files/dashboard-config/dashboard-no-auth.yaml
kubectl apply -f https://raw.githubusercontent.com/kubernetes/dashboard/master/src/deploy/recommended/kubernetes-dashboard.yaml

#echo "Launch kubernets dashboard"
# Launch the dahsboard using the IP from the same host as the api-server 
#echo "  http://10.0.0.11:9999/api/v1/namespaces/kube-system/services/https:kubernetes-dashboard:/proxy/#!/login"
#export K8S_NODE_IP=$(ifconfig enp0s8 | grep "inet addr" | cut -d ':' -f 2 | cut -d ' ' -f 1)
#kubectl proxy --port=9999 --address=$K8S_NODE_IP --accept-hosts="^*$" &





