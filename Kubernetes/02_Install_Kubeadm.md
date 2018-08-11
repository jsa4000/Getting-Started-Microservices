# Kubernetes

## Introduction

**Kubernetes** (commonly stylized as **K8s**) is an open-source **container-orchestration** system for automating deployment, scaling and management of containerized applications. It was originally designed by Google and is now maintained by the Cloud Native Computing Foundation. It aims to provide a *platform for automating deployment, scaling, and operations of application containers across clusters of hosts.* It works with a range of container tools, including **Docker**.

Kubernetes defines a set of building blocks or primitives, which collectively provide mechanisms that deploy, maintain, and scale applications. Kubernetes are loosely coupled and extensible to meet different workloads. This extensibility is provided in large part by the Kubernetes API, which is used by internal components as well as extensions and containers that run on Kubernetes.

### Pods

The basic scheduling unit in Kubernetes is a **pod**. It adds a higher level of abstraction by grouping containerized components. A **pod** consists of **one** or **more** containers that are guaranteed to be co-located on the host machine and can share resources. Each pod in Kubernetes is assigned a **unique IP** address within the cluster, which allows applications to use ports without the risk of conflict. A pod can define a **volume**, such as a local disk directory or a network disk, and expose it to the containers in the pod. Pods can be managed manually through the Kubernetes API, or their management can be delegated to a controller.

### Labels and selectors

Kubernetes enables clients (users or internal components) to attach **key-value** pairs called **labels** to any API object in the system, such as pods and nodes. Correspondingly, **label selectors** are **queries** against labels that resolve to matching objects.

**Labels** and **selectors** are the primary **grouping** mechanism in Kubernetes, and **determine** the components an operation applies to.

For example, if an application's pods have labels for a system tier (front-end, back-end, for example) and a release_track (canary, production, for example), then an operation on all of back-end and canary nodes can use a label selector, such as:

    tier=back-end AND release_track=canary

### Controllers

A **controller** is a reconciliation loop that **drives** actual cluster state toward the **desired** cluster **state**. It does this by **managing** a **set** of **pods**.

One kind of controller is a **replication** controller, which handles **replication** and **scaling** by running a specified number of copies of a pod across the cluster. It also handles **creating** replacement pods if the underlying node **fails**.

Other controllers that are part of the core Kubernetes system include a **DaemonSet Controller** for running exactly one pod on every machine (or some subset of machines), and a **Job Controller** for running pods that run to completion, e.g. as part of a batch job. The set of pods that a controller manages is determined by label selectors that are part of the controller’s definition.

### Services

A Kubernetes **service** is a **set of pods** that work **together**, such as one tier of a multi-tier application. The set of pods that constitute a service are **defined** by a **label selector**. Kubernetes provides **service discovery** and **request routing** by assigning a **stable** IP address and DNS name to the service, and **load balances** traffic in a round-robin manner to network connections of that IP address among the pods matching the selector (even as failures cause the pods to move from machine to machine). By default a service is exposed inside a cluster (e.g. back end pods might be grouped into a service, with requests from the front-end pods load-balanced among them), but a service can also be exposed outside a cluster (e.g. for clients to reach frontend pods).

### Components

#### Master Components

Master components provide the cluster’s control plane. Master components make global decisions about the cluster (for example, scheduling), and detecting and responding to cluster events (starting up a new pod when a replication controller’s ‘replicas’ field is unsatisfied).

- **kube-apiserver**: Component on the master that exposes the Kubernetes API. It is the front-end for the Kubernetes control plane. It is designed to scale horizontally – that is, it scales by deploying more instances.

- **etcd**: Consistent and highly-available key value store used as Kubernetes’ backing store for all cluster data. Always have a backup plan for etcd’s data for your Kubernetes cluster. For in-depth information on etcd, see etcd documentation.

- **kube-scheduler**: Component on the master that watches newly created pods that have no node assigned, and selects a node for them to run on. Factors taken into account for scheduling decisions include individual and collective resource requirements, hardware/software/policy constraints, affinity and anti-affinity specifications, data locality, inter-workload interference and deadlines.

- **kube-controller-manager**: Component on the master that runs controllers. Logically, each controller is a separate process, but to reduce complexity, they are all compiled into a single binary and run in a single process.

- **cloud-controller-manager**: cloud-controller-manager runs controllers that interact with the underlying cloud providers. 

#### Node Components

Node components run on every node, maintaining running pods and providing the Kubernetes runtime environment.

- **kubelet**: An agent that runs on each node in the cluster. It makes sure that containers are running in a pod. The kubelet takes a set of PodSpecs that are provided through various mechanisms and ensures that the containers described in those PodSpecs are running and healthy. The kubelet doesn’t manage containers which were not created by Kubernetes.

- **kube-proxy**: kube-proxy enables the Kubernetes service abstraction by maintaining network rules on the host and performing connection forwarding.

- **Container Runtime**: The container runtime is the software that is responsible for running containers. Kubernetes supports several runtimes: Docker, rkt, runc and any OCI runtime-spec implementation.

#### Addons

Addons are pods and services that implement cluster features. The pods may be managed by Deployments, ReplicationControllers, and so on. Namespaced addon objects are created in the kube-system namespace.

- **DNS**: all Kubernetes clusters should have cluster DNS. Cluster DNS is a DNS server, in addition to the other DNS server(s) in your environment, which serves DNS records for Kubernetes services. Containers started by Kubernetes automatically include this DNS server in their DNS searches.

- **Web UI (Dashboard)**: Dashboard is a general purpose, web-based UI for Kubernetes clusters. It allows users to manage and troubleshoot applications running in the cluster, as well as the cluster itself.

- **Container Resource Monitoring**: Container Resource Monitoring records generic time-series metrics about containers in a central database, and provides a UI for browsing that data.

- **Cluster-level Logging**: A Cluster-level logging mechanism is responsible for saving container logs to a central log store with search/browsing interface.

## Installation

> Refer to the following guide on [How to Install kubeadm](https://kubernetes.io/docs/setup/independent/install-kubeadm/)

### Pre-requisites

- Linux machine, current supported distributions are: Ubuntu 16.04+, Debian 9, CentOS 7, RHEL 7, Fedora 25/26 (best-effort), HypriotOS v1.0.1+, Container Linux (tested with 1576.4.0)
- 2 GB or more of RAM per machine (any less will leave little room for your apps)
- 2 CPUs or more
- Verify the **MAC address** and **product_uuid** are unique for every node

  - You can get the MAC address of the network interfaces using the command

        ip link or ifconfig -a

  - The product_uuid can be checked by using the command 

        sudo cat /sys/class/dmi/id/product_uuid

- **Swap** must be **disabled**. You can check if you have swap enabled by typing in:

        cat /proc/swaps

  If you have a swap file or partition enabled then turn it off with **swapoff**. You can make this permanent by commenting out the swap file in:
  
        /etc/fstab.

### K8s Components

K8s need following packages on **all** of your machines:

- kubeadm: the command to bootstrap the cluster.
- kubelet: the component that runs on all of the machines in your cluster and does things like starting pods and containers.
- kubectl: the command line util to talk to your cluster.
- kubernetes-cni: represents the networking components.

> **CNI** stands for **Container Networking Interface** which is a spec that defines how network drivers should interact with Kubernetes.

#### Installing kubeadm, kubelet and kubectl

> [Kubernetes in 10 minutes](https://blog.alexellis.io/kubernetes-in-10-minutes/​)

kubeadm will **not** install or manage **kubelet** or **kubectl**, so all dependencies must be installed:

    sudo apt-get update && apt-get install -y apt-transport-https curl
    sudo curl -s https://packages.cloud.google.com/apt/doc/apt-key.gpg | sudo apt-key add -

    sudo chattr -i /etc/apt/sources.list.d/kubernetes.list
    sudo cat <<EOF >/etc/apt/sources.list.d/kubernetes.list
    deb http://apt.kubernetes.io/ kubernetes-xenial main
    EOF

    sudo apt-get update
    sudo apt-get install -y kubelet kubeadm kubectl

Error while Updating kubernets respository list

    echo "deb http://apt.kubernetes.io/ kubernetes-xenial main" \
    | sudo tee -a /etc/apt/sources.list.d/kubernetes.list \
    && sudo apt-get update

#### Initializing Kubernetes Cluster

To initialize kubernetes cluster, it must be used the internal IP address to broadcast the Kubernetes API - rather than the Internet-facing address.

> It must be replaced *--apiserver-advertise-address* with the IP of your host.

    sudo kubeadm init --pod-network-cidr=10.100.0.0/16 --apiserver-advertise-address=10.0.0.11 --kubernetes-version stable-1.8

Or without specifying the kubernetes version to deploy.

    sudo kubeadm init --pod-network-cidr=10.100.0.0/16 --apiserver-advertise-address=10.0.0.11

Where:

- **apiserver-advertise-address**: determines which IP address Kubernetes should advertise its API server on.

- **pod-network-cidr**: is needed for the flannel driver and specifies an address space for containers

- **skip-preflight-check**: allows kubeadm to check the host kernel for required features. If you run into issues where a host has the kernel meta-data removed you may need to run with this flag.

- **kubernetes-version**: stable-1.8 this pins the version of the cluster to 1.8, but if you want to use Kubernetes 1.7 for example - then just alter the version. Removing this flag will use whatever counts as "latest"

To quickly start kubeadm, it must be configured the **-–pod-network-cidr** switch (Internal Network or VPC):

    sudo kubeadm init --pod-network-cidr=192.168.0.0/16

To reset the configuration (failed)

    sudo kubeadm reset

> After this operation all the configuration done to the master must be redone again: *.kube/config*, *CNI*, etc..

After the installation, you must see the following output

```txt
Your Kubernetes master has initialized successfully!

To start using your cluster, you need to run the following as a regular user:

  mkdir -p $HOME/.kube
  sudo cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
  sudo chown $(id -u):$(id -g) $HOME/.kube/config

You should now deploy a pod network to the cluster.
Run "kubectl apply -f [podnetwork].yaml" with one of the options listed at:
  https://kubernetes.io/docs/concepts/cluster-administration/addons/

You can now join any number of machines by running the following on each node
as root:

    kubeadm join 10.0.0.11:6443 --token 0hc275.02myvage7a36tknu --discovery-token-ca-cert-hash sha256:1b9bba62b5987534914c3f90bd65e5751a72571b6503a531407b2dd859ba7066

```

As the installation said, to start using the cluster, it must be used the following as a regular user:

    mkdir -p $HOME/.kube
    sudo cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
    sudo chown $(id -u):$(id -g) $HOME/.kube/config

> **Kubectl** can be **managed** in **slave nodes** also using the same config file and previous commands.

To verify current installation has been succcesfully installed, use the following commands

    sudo kubectl get nodes
    sudo kubectl get pods --all-namespaces

    sudo docker ps

#### Installing unprivileged user-account

Packet's Ubuntu installation ships without an unprivileged user-account, so let's add one.

    sudo useradd packet -G sudo -m -s /bin/bash
    sudo passwd packet

Configure environmental variables as the new user

You can now configure your environment with the instructions at the end of the init message above.

Switch into the new user account with:

    sudo su packet

    cd $HOME
    sudo whoami

    sudo cp /etc/kubernetes/admin.conf $HOME/
    sudo chown $(id -u):$(id -g) $HOME/admin.conf
    export KUBECONFIG=$HOME/admin.conf

    echo "export KUBECONFIG=$HOME/admin.conf" | tee -a ~/.bashrc

#### Installing Container Networking Interface

**Flannel** provides a software defined network (SDN) using the Linux kernel's overlay and ipvlan modules.

> Another popular SDN offering is Weave Net by WeaveWorks. [Find out more here](https://www.weave.works/docs/net/latest/kubernetes/kube-addon/).

    sudo kubectl apply -f https://raw.githubusercontent.com/coreos/flannel/master/Documentation/kube-flannel.yml
    sudo kubectl apply -f https://raw.githubusercontent.com/coreos/flannel/master/Documentation/k8s-manifests/kube-flannel-rbac.yml

After applying **CNI** to the K8s cluster, you must see the following output when getting all nodes.

    sudo kubectl get nodes

The output for the previous command must be the following:

```txt
NAME         STATUS    ROLES     AGE       VERSION
k8s-master   Ready     master    32m       v1.11.1
```

    sudo kubectl get pods --all-namespaces

The output for the previous command must be the following:

```txt
NAMESPACE     NAME                                 READY     STATUS    RESTARTS   AGE
kube-system   coredns-78fcdf6894-4575g             1/1       Running   2          37m
kube-system   coredns-78fcdf6894-gs7gs             1/1       Running   2          37m
kube-system   etcd-k8s-master                      1/1       Running   0          6m
kube-system   kube-apiserver-k8s-master            1/1       Running   0          6m
kube-system   kube-controller-manager-k8s-master   1/1       Running   0          6m
kube-system   kube-flannel-ds-amd64-wvt52          1/1       Running   0          6m
kube-system   kube-proxy-b24sr                     1/1       Running   0          37m
kube-system   kube-scheduler-k8s-master            1/1       Running   0          6m
```

#### Configure master for self-host node

Kubernetes is about multi-host clustering - so by default containers **cannot** run on master nodes in the cluster. Since we only have one node - we'll taint it so that it can run containers for us.

    sudo kubectl taint nodes --all node-role.kubernetes.io/master-

Check it's working. Many of the Kubernetes components run as containers on your cluster in a hidden namespace called kube-system. You can see whether they are working like this.

    sudo kubectl get all --namespace=kube-system

#### Join new nodes to K8s cluster

In order to add newer nodes to the kubernetes cluster, it must be used the **join** command was prompted after the master node started.

    kubeadm join 10.0.0.11:6443 --token zx5pxb.wtdyahomzrbv0j8r --discovery-token-ca-cert-hash sha256:4af2ff961db5b67cb08e80a465ef043a4e3712c4b8a0845412e2a837d2fc8fa9

> Be sure the IP used in the join is accessed from the node that is going to be joind to the k8s cluster.

The output received must be the following from the nodes

```txt
This node has joined the cluster:
* Certificate signing request was sent to master and a response
  was received.
* The Kubelet was informed of the new secure connection details.
```

Run the following command on the master to see this node join the cluster.

    sudo kubectl get nodes

The output must be similar to that one, with all the nodes joined to the cluster

```txt
NAME         STATUS    ROLES     AGE       VERSION
k8s-master   Ready     master    10m       v1.11.1
k8s-node01   Ready     <none>    6m        v1.11.1
k8s-node02   Ready     <none>    3m        v1.11.1
```

Run, the following command to get all the pods, instances and status

    sudo kubectl get pods --all-namespaces

When fetching all the pods, it could be seen duplitated pods depending on the nodes joined to the k8s cluster (*kube-flannel* and *kube-proxy*).

```txt
NAMESPACE     NAME                                 READY     STATUS             RESTARTS   AGE
kube-system   coredns-78fcdf6894-2d4gr             1/1       Running            41         3h
kube-system   coredns-78fcdf6894-pxk8t             1/1       Running            41         3h
kube-system   etcd-k8s-master                      1/1       Running            0          3h
kube-system   kube-apiserver-k8s-master            1/1       Running            0          3h
kube-system   kube-controller-manager-k8s-master   1/1       Running            0          3h
kube-system   kube-flannel-ds-amd64-m5hr4          1/1       Running            0          3h
kube-system   kube-flannel-ds-amd64-v4qcd          1/1       Running            36         3h
kube-system   kube-flannel-ds-amd64-wnqmw          0/1       CrashLoopBackOff   36         3h
```

#### View the Dashboard UI

[Web UI Dashboard](https://kubernetes.io/docs/tasks/access-application-cluster/web-ui-dashboard/)

The Kubernetes dashboard can be deployed as another Pod, which we can then view on our local machine. Since we did not expose Kubernetes over the Internet we'll use an SSH tunnel to view the site.

    kubectl apply -f https://raw.githubusercontent.com/kubernetes/dashboard/master/src/deploy/recommended/kubernetes-dashboard.yaml
    kubectl apply -f https://raw.githubusercontent.com/kubernetes/dashboard/master/src/deploy/recommended/kubernetes-dashboard-head.yaml

- To remove the dashboard (or stop the pods running)

      kubectl delete -f https://raw.githubusercontent.com/kubernetes/dashboard/master/src/deploy/recommended/kubernetes-dashboard.yaml

- To find the master ip address run the following

      kubectl config view

- Run the following (--address is the IP of the master ip address) to start the dashboard

      kubectl proxy --port=9999 --address='master.ip.address' --accept-hosts="^*$"

      kubectl proxy --port=9999 --address='10.0.0.11' --accept-hosts="^*$"

- Go to your browser and put following URLs:

  - [Kubernetes API](http://10.0.0.11:9999/ui)
  - [Kubernetes Dashboard](http://10.0.0.11:9999/api/v1/namespaces/kube-system/services/https:kubernetes-dashboard:/proxy/#!/login)

- Kubernets dashboard need authentication. Following are the commands to get the **token** to authenticate manually:

  > It can be used also *kubernetes-dashboard-head.yaml*
  > [Creating Admin User](https://github.com/kubernetes/dashboard/wiki/Creating-sample-user)

  - Create role binding *dashboard-admin.yaml*.

  ```yml
  apiVersion: v1
  kind: ServiceAccount
  metadata:
    name: admin-user
    namespace: kube-system
    ---
  apiVersion: rbac.authorization.k8s.io/v1beta1
  kind: ClusterRoleBinding
  metadata:
    name: admin-user
  roleRef:
    apiGroup: rbac.authorization.k8s.io
    kind: ClusterRole
    name: cluster-admin
  subjects:
  - kind: ServiceAccount
    name: admin-user
    namespace: kube-system
  ```

  - Apply custorm priviledge.

        kubectl create -f dashboard-admin.yaml

  - Perform the following search over all the screts created to find the previous one created

        kubectl -n kube-system describe secret $(kubectl -n kube-system get secret | grep admin-user | awk '{print $1}')

    > Search for the previous *kubernetes.io/service-account-token* will allow to log in.
    > Note that they have different privileges.

  - Additionally, it could be used (teorically one of the following secrets previously created)
        kubectl -n kube-system get secret
        kubectl -n kube-system describe secret kubernetes-dashboard-head-token-h4fww

```txt
Name:         admin-user-token-4xk5m
Namespace:    kube-system
Labels:       <none>
Annotations:  kubernetes.io/service-account.name=admin-user
              kubernetes.io/service-account.uid=702c2afb-8f0b-11e8-a6eb-02fea1ea7dd1

Type:  kubernetes.io/service-account-token

Data
====
namespace:  11 bytes
token:      eyJhbGciOiJSUzI1NiIsImtpZCI6IiJ9.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJrdWJlLXN5c3RlbSIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VjcmV0Lm5hbWUiOiJhZG1pbi11c2VyLXRva2VuLTR4azVtIiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZXJ2aWNlLWFjY291bnQubmFtZSI6ImFkbWluLXVzZXIiLCJrdWJlcm5ldGVzLmlvL3NlcnZpY2VhY2NvdW50L3NlcnZpY2UtYWNjb3VudC51aWQiOiI3MDJjMmFmYi04ZjBiLTExZTgtYTZlYi0wMmZlYTFlYTdkZDEiLCJzdWIiOiJzeXN0ZW06c2VydmljZWFjY291bnQ6a3ViZS1zeXN0ZW06YWRtaW4tdXNlciJ9.FcgKkwI8sCZiSzLEGdFdt0Zlzu_GJHSI62oNiXtXCnX_iyJrPeEkIPLfcGdWGG0obPF-OLgLdQj2QnjVtpu-g6j2OwlgfcYTedTzJ-kojxoLGTbomTXYNxOkQwQChHV4L479t4AwAPcBAn6wQQnObPdW2KRIuvhUkIipm-BT684jw7-Pw536TFNyVqNCuIbweW6O_PcTe3DE7pYAlzV4YjZ6m1yspoKI4zz8AomAD2ziOYmz62fIxMtk2GJKMuNuTCmyt7W2ZOPGeT5H8wEjEFUb4ZZY4NoxFCcm4-_FPPla_aT1CiJ98wESbom7uz9KMnjawRTSJGKpzJCgKHgjbQ
```

To **remove** the authentication totally per96orm the following steps:

> [Steps To remove the authentication in Kub96rnetes Dashboard](https://www.assistanz.com/steps-to-install-kubernetes-dashboard/)

- Create a file *dashboard-admin.yaml*

  ```yml
  apiVersion: rbac.authorization.k8s.io/v1beta1
  kind: ClusterRoleBinding
  metadata:
     name: kubernetes-dashboard
     labels:
       k8s-app: kubernetes-dashboard
  roleRef:
     apiGroup: rbac.authorization.k8s.io
     kind: ClusterRole
     name: cluster-admin
  subjects:
  - kind: ServiceAccount
    name: kubernetes-dashboard
    namespace: kube-system
  ```

  - Run following command to create the role
  
        kubectl apply -f dashboard-admin.yaml

  - Start the server with the dashboard

        kubectl proxy --port=9999 --address='10.0.0.11' --accept-hosts="^*$" &

    > Use *&* to execute the process in the background

  - Connect to the dashboard and press **Skip** button

        http://10.0.0.11:9999/api/v1/namespaces/kube-system/services/https:kubernetes-dashboard:/proxy/#!/login

To delete *proxy* process (proxy)

    ps -a
    kill -9 <process ID>

## Basic Usage

### Run a Container

From Kubernetes it can be created containers that are going to be managed by Kubernetes, instead of being created using Docker. This allows kebernets the proper orchestration and management for all containers created inside the cluster.

Following command creates a pod with the nginx container.

    kubectl run nginx-http --image=nginx:latest --port 8080

To get the name assigned to the port use

    kubectl get pods

```txt
NAME                     READY     STATUS              RESTARTS   AGE
nginx-http-7f6bf97964-f7qtg   0/1       ContainerCreating   0          1m
```

To get more detailed information use the describe (node|pod|image) and the *Name* of the pod.

    kubectl describe pod nginx-http-7f6bf97964-f7qtg

```txt
Name:               nginx-http-7f6bf97964-tcnbm
Namespace:          default
Priority:           0
PriorityClassName:  <none>
Node:               k8s-node02/10.0.2.15
Start Time:         Mon, 23 Jul 2018 13:22:25 +0000
Labels:             pod-template-hash=3926953520
                    run=nginx-http
Annotations:        <none>
Status:             Running
IP:                 10.100.2.2
Controlled By:      ReplicaSet/nginx-http-7f6bf97964
Containers:
  nginx-http:
    Container ID:   docker://07135f3e9859b99e232461b1b23f541bdfa80b9764d0cf8790b25c80a3346fd1
    Image:          nginx:latest
    Image ID:       docker-pullable://nginx@sha256:4a5573037f358b6cdfa2f3e8a9c33a5cf11bcd1675ca72ca76fbe5bd77d0d682
    Port:           8080/TCP
    Host Port:      0/TCP
    State:          Running
      Started:      Mon, 23 Jul 2018 13:22:34 +0000
    Ready:          True
    Restart Count:  0
    Environment:    <none>
    Mounts:
      /var/run/secrets/kubernetes.io/serviceaccount from default-token-5q25n (ro)
Conditions:
  Type              Status
  Initialized       True 
  Ready             True 
  ContainersReady   True 
  PodScheduled      True 

```