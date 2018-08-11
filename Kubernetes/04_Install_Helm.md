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

I **Tiller** is alread installed, directly configuire the address

```txt
vagrant@k8s-master:~$ kubectl get services --all-namespaces
NAMESPACE     NAME            TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)         AGE
default       kubernetes      ClusterIP   10.96.0.1        <none>        443/TCP         5h
kube-system   kube-dns        ClusterIP   10.96.0.10       <none>        53/UDP,53/TCP   5h
kube-system   tiller-deploy   ClusterIP   10.111.121.213   <none>        44134/TCP       6m
```

    export HELM_HOST=10.111.121.213:44134

Verify that you have the correct version and that it installed properly by running:

    helm version

To update helm

    helm init --upgrade

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

This can be done also after the init typing following commands. This solves the error *Error: no available release name found*

    kubectl create serviceaccount --namespace kube-system tiller
    kubectl create clusterrolebinding tiller-cluster-rule --clusterrole=cluster-admin --serviceaccount=kube-system:tiller
    kubectl patch deploy --namespace kube-system tiller-deploy -p '{"spec":{"template":{"spec":{"serviceAccount":"tiller"}}}}'

## Redirect Ports

    kubectl -n kube-system port-forward $(kubectl -n kube-system get pod -l app=helm -o jsonpath='{.items[0].metadata.name}') 44134

    kubectl --namespace kube-system get pod -o wide

### Remove Triller

 The recommended way of deleting Tiller is with

    kubectl delete deployment tiller-deploy --namespace kube-system
    kubectl service deployment tiller-deploy --namespace kube-system

or more concisely

    helm reset

## Example Chart

To install a chart, you can run the helm **install** command. Helm has several ways to find and install a chart, but the easiest is to use one of the official stable charts.

First, make sure we get the latest list of charts

    helm repo update

To list all available charts on the reposiory run the following command

    helm search

Install **mysql** chart

    helm install stable/mysql

    Released smiling-penguin

In the example above, the stable/mysql chart was released, and the name of our new release is smiling-penguin. You get a simple idea of the features of this MySQL chart by running

    helm inspect stable/mysql

Whenever you install a chart, a new release is created. So one chart can be installed multiple times into the same cluster. And each can be independently managed and upgraded.

The helm install command is a very powerful command with many capabilities. To learn more about it, check out the Using Helm Guide

## Issues

The issues I had above were caused by not having registered the node names in local DNS. The fix was to add --kubelet-preferred-address-types=InternalIP to the apiserver manifest.

## References

- [Official Helm Website](https://helm.sh/)
- [Quickstart Guide](https://docs.helm.sh/using_helm/#quickstart-guide)
- [How To install Helm](http://zero-to-jupyterhub.readthedocs.io/en/latest/setup-helm.html)