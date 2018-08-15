#!/usr/bin/env bash
echo "- Installing Helm Package Management"

# Download helm from Github repository
curl https://raw.githubusercontent.com/kubernetes/helm/master/scripts/get | bash

# Setup Helm on the cluster
helm init
helm init --upgrade
helm repo update

# Create Service Account for Tiller
kubectl create serviceaccount --namespace kube-system tiller
kubectl create clusterrolebinding tiller-cluster-rule --clusterrole=cluster-admin --serviceaccount=kube-system:tiller
kubectl patch deploy --namespace kube-system tiller-deploy -p '{"spec":{"template":{"spec":{"serviceAccount":"tiller"}}}}'