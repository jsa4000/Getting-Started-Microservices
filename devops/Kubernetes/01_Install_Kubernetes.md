# Installation

> Kubernetes (commonly referred to as "K8s") is an open-source system for automating deployment, scaling and management of containerized applications that was originally designed by Google and donated to the Cloud Native Computing Foundation. It aims to provide a "platform for automating deployment, scaling, and operations of application containers across clusters of hosts". It supports a range of container tools, including Docker.

## Basic Terminology

The installation uses a tool called kubeadm which is part of Kubernetes. As of v1.6, kubeadm aims to create a secure cluster out of the box via mechanisms  such as RBAC.
  
- docker: the container runtime, which Kubernetes depends on.
- kubelet: the most core component of Kubernetes. It runs on all of the machines in your - cluster and does things like starting pods and containers.
- kubectl: the command to control the cluster once it’s running. You will only use this on the master.
- kubeadm: the command to bootstrap the cluster.
- etcd: etcd is a distributed reliable key-value store for the most critical data of a distributed system.

## Installing kubelet and kubeadm on your hosts

> If you already have **kubeadm** installed, you should do a apt-get update && apt-get upgrade or yum update to get the latest version of kubeadm.

This must be done for every Kubernet Node that will be installed.

SSH into the machine and become root if you are not already (for example, run sudo -i)

    ~$ apt-get update && apt-get install -y apt-transport-https
    ~$ curl -s https://packages.cloud.google.com/apt/doc/apt-key.gpg | apt-key add -
    ~$ cat <<EOF >/etc/apt/sources.list.d/kubernetes.list
    deb http://apt.kubernetes.io/ kubernetes-xenial main
    EOF
    ~$ apt-get update
    ~$ apt-get install -y kubelet kubeadm

> Note: Disabling SELinux by running setenforce 0 is required to allow containers to access the host filesystem, which is required by pod networks for  example. You have to do this until SELinux support is improved in the kubelet.

## Initializing your master

To initialize the **master**, pick one of the machines you previously installed kubelet and kubeadm on, and run:

    ~$ kubeadm init

This will download and install the cluster database and “control plane” components. Finally **kubeadm init** will provide a **kubeadm join** command. he key  is used for mutual authentication between the master and the joining nodes.

    Kubernetes master initialised successfully!

You can connect any number of nodes by running:

    kubeadm join --token <token> <master-ip>
    kubeadm join --token dc629d.249a1cb843c28f0a 192.168.1.142:6443

To test the installation and running. First you will need to install

    ~$ kubectl get nodes
    ~$ kubectl get pods

> The connection to the server localhost:8080 was refused - did you specify the right host or port?

To start using your cluster, you need to run (as a regular user):

    ~$ sudo cp /etc/kubernetes/admin.conf $HOME/
    ~$ sudo chown $(id -u):$(id -g) $HOME/admin.conf
    ~$ export KUBECONFIG=$HOME/admin.conf

> By default, your cluster will **not schedule pods** on the **master** for security reasons. If you want to be able to schedule pods on the master, for  example if you want a single-machine Kubernetes cluster for development, run:

    ~$ kubectl taint nodes --all dedicated-

## Installing a pod network

You must install a pod network add-on so that your pods can communicate with each other.

The network must be deployed before any applications. Also, kube-dns, a helper service, will not start up before a network is installed. kubeadm only  supports Container Network Interface (CNI) based networks (and does not support kubenet).

You can install a pod network add-on with the following command:

    ~$ kubectl apply -f <add-on.yaml>

    ~$ kubectl apply -f https://raw.githubusercontent.com/coreos/flannel/master/Documentation/kube-flannel.yml

## Joining your nodes

The nodes are where your workloads (containers and pods, etc) run. To add new nodes to your cluster do the following for each machine:

- SSH to the machine
- Become root (ie. sudo su -)
- Run the command that was output by kubeadm init. For example:

        ~$ kubeadm join --token <token> <master-ip>:<master-port>
        ~$ kubectl get nodes

## Controlling your cluster from machines other than the master

In order to get a kubectl on some other computer (e.g. laptop) to talk to your cluster, you need to copy the administrator kubeconfig file from your master to your workstation like this:

    scp root@<master ip>:/etc/kubernetes/admin.conf .
    kubectl --kubeconfig ./admin.conf get nodes
    kubectl --namespace=kube-system describe pod kube-dns-2924299975-dfp17

## Installing MiniKube

1. Install dependencies (virstual-box)

        sudo apt-get install virtualbox virtualbox-ext-pack

1. Install Kubectl

        sudo apt-get update && sudo apt-get install -y apt-transport-https
        curl -s https://packages.cloud.google.com/apt/doc/apt-key.gpg | sudo apt-key add -
        sudo touch /etc/apt/sources.list.d/kubernetes.list 
        echo "deb http://apt.kubernetes.io/ kubernetes-xenial main" | sudo tee -a /etc/apt/sources.list.d/kubernetes.list
        sudo apt-get update
        sudo apt-get install -y kubectl

1. Install Minikube

        curl -Lo minikube https://storage.googleapis.com/minikube/releases/v0.28.2/minikube-linux-amd64 && chmod +x minikube && sudo mv minikube /usr/local/bin/

1. Start Minikube

        # Basic command to start minikube cluster
        minikube start

        # Required to specify the **VM** driver to install
        minikube start --vm-driver="virtualbox"

        # Specify VM capacity such as memory or CPUs
        minikube start --disk-size 80g --cpus 3 --memory 8192

1. Verify the installation

    minikube version
    kubectl version

    minikube status
    kubectl.exe cluster-info

1. Start the dashboard

        minikube dashboard

1. Enable addons for **ingress** and **kube-dns** support for helm charts and deployments

    minikube addons enable ingress
    minikube addons enable kube-dns

1. Install helm

        # Download and setup helm
        curl https://raw.githubusercontent.com/kubernetes/helm/master/scripts/get

        # Initialize helm and intall tiller on kubernetes cluster
        helm init

        # Verify the vesrion
        helm version

1. Check the current pods installed.

    kubetl get pods --all-namespaces

1. Delete Minikube

    minikube delete

    > Delete Minikube is needed if variables are changed such as memory, cpus, etc. in order to be applied

> Minikube doesn not work with **VirtualBox** since it has not implementet ``VT-X/AMD-v`` for virtualization.

## References

- [Getting started with Kubeadm](https://lukemarsden.github.io/docs/getting-started-guides/kubeadm/)
