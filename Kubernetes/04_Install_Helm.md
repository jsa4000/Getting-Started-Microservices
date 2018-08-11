# HELM

## Introduction

**Helm** is the *package manager* for **Kubernetes**. It is used to **upgrade** and **manage** applications on a Kubernetes cluster.

**Helm** works by initializing itself both **locally** (on your computer) and **remotely** (on your kubernetes cluster).

Helm commands sends instructions from helm client to the **Tiller**, which exists on your Kubernetes cluster, and is controlled by the server-side helm install.

## Installation

The simplest way to install helm is to run Helm’s installer script at a terminal:

    curl https://raw.githubusercontent.com/kubernetes/helm/master/scripts/get | bash

Setup Helm on the cluster

    helm init

Verify that you have the correct version and that it installed properly by running:

    helm version

### Secure Helm (pre-initialization)

Set up a ServiceAccount for use by Tiller, the server side component of helm.

    kubectl --namespace kube-system create serviceaccount tiller

Give the ServiceAccount RBAC full permissions to manage the cluster.

> While most clusters have RBAC enabled and you need this line, you must skip this step if your kubernetes cluster does not have RBAC enabled 

    kubectl create clusterrolebinding tiller --clusterrole cluster-admin --serviceaccount=kube-system:tiller

Set up Helm on the cluster.

    helm init --service-account tiller

Ensure that tiller is secure from access inside the cluster:

    kubectl --namespace=kube-system patch deployment tiller-deploy --type=json --patch='[{"op": "add", "path": "/spec/template/spec/containers/0/command", "value": ["/tiller", "--listen=localhost:44134"]}]'

## Example Chart

To install a chart, you can run the helm **install** command. Helm has several ways to find and install a chart, but the easiest is to use one of the official stable charts.

First, make sure we get the latest list of charts

    helm repo update

Install **mysql** chart

    helm install stable/mysql

    Released smiling-penguin

In the example above, the stable/mysql chart was released, and the name of our new release is smiling-penguin. You get a simple idea of the features of this MySQL chart by running

    helm inspect stable/mysql.

Whenever you install a chart, a new release is created. So one chart can be installed multiple times into the same cluster. And each can be independently managed and upgraded.

The helm install command is a very powerful command with many capabilities. To learn more about it, check out the Using Helm Guide