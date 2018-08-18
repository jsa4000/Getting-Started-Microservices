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

This can be done also after the init typing following commands. This solves the error **Error: no available release name found**

    kubectl create serviceaccount --namespace kube-system tiller
    kubectl create clusterrolebinding tiller-cluster-rule --clusterrole=cluster-admin --serviceaccount=kube-system:tiller
    kubectl patch deploy --namespace kube-system tiller-deploy -p '{"spec":{"template":{"spec":{"serviceAccount":"tiller"}}}}'

### Client Installation

If **Tiller** is alread installed, directly configure the address

```txt
vagrant@k8s-master:~$ kubectl get services --all-namespaces
NAMESPACE     NAME            TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)         AGE
default       kubernetes      ClusterIP   10.96.0.1        <none>        443/TCP         5h
kube-system   kube-dns        ClusterIP   10.96.0.10       <none>        53/UDP,53/TCP   5h
kube-system   tiller-deploy   ClusterIP   10.111.121.213   <none>        44134/TCP       6m
```

Configure the environment variable **HELM_HOS** where is placed **tiller** service.

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

### Redirect Ports

    kubectl -n kube-system port-forward $(kubectl -n kube-system get pod -l app=helm -o jsonpath='{.items[0].metadata.name}') 44134

    kubectl --namespace kube-system get pod -o wide

### Remove Triller

 The recommended way of deleting Tiller is with

    kubectl delete deployment tiller-deploy --namespace kube-system
    kubectl delete service tiller-deploy --namespace kube-system

or more concisely

    helm reset

## Example Chart

To install a chart, you can run the helm **install** command. Helm has several ways to find and install a chart, but the easiest is to use one of the official stable charts.

First, make sure we get the latest list of charts

    helm repo update

To list all available charts on the reposiory run the following command

    helm search

### Install MySQL (static volume)

In order to install any **chart**, the command is basically the following

    helm install stable/mysql

Depending if we are working on **cloud** or **bare-metal** environments, it depends the way **PersistenceVolume** (**PV**) and **PersistenceVolumeClaim** (**PVC**) are managed automatically by k8s. There are multiple types of storages, depending on **Dynamic types** or **persistance volumes**. The abstraction for both concepts is the PVC (**Persistance Volume Class**). In this [link](https://kubernetes.io/docs/concepts/storage/persistent-volumes/) you can take a look the **StorageClass** supported by kubernetes.

For bare-metal environment it is neccesary to create manually the persistance volumne. In this exampla the storageTypeClass **local** is going to be defined. This is passed through commands to helm when installing the package and used by PersistanceVolumeClaim to bind the volume.

- Create the Persistance Volumne *pv-volumes.yaml*

> This directories should be created on the storage server or node selected (**nodeSelector=k8s-node2**) previously.

    ```yml
    kind: PersistentVolume
    apiVersion: v1
    metadata:
      name: pv-00001
      labels:
        type: local
    spec:
      storageClassName: local
      capacity:
        storage: 10Gi
      accessModes:
        - ReadWriteOnce
      hostPath:
        path: "/mnt/data/00001"
    ---
    kind: PersistentVolume
    apiVersion: v1
    metadata:
      name: pv-00002
      labels:
        type: local
    spec:
      storageClassName: local
      capacity:
        storage: 10Gi
      accessModes:
        - ReadWriteOnce
      hostPath:
        path: "/mnt/data/00002"
    ```

- Apply this file to kubernetes

        sudo kubectl create -f pv-volumes.yaml

        sudo kubectl get pv
        sudo kubectl describe pv/pv-00001

- Create the directories for persistence previously defined

        vagrant ssh k8s-node2

        sudo mkdir /mnt/data/00001
        sudo mkdir /mnt/data/00001

- Install the helm package providing the information *(**storageClassName**)* created on **pv-volumes.yaml**. This autmatically will claim for an available store, in this case the previous store created.

> Note in the command the parameter **nodeSelector=k8s-node2** has been used to create the pods in the same node since the persistence resides in that node. In the next section shared storages will be seen for more advance and neat features.

    sudo helm install --name mysql-db --set mysqlRootPassword=secretpassword,mysqlUser=admin,mysqlPassword=admin,mysqlDatabase=default-schema,persistence.storageClass=local stable/mysql

    ```txt
    NAME:   pouring-snail
    LAST DEPLOYED: Sun Aug 12 07:14:26 2018
    NAMESPACE: default
    STATUS: DEPLOYED

    RESOURCES:
    ==> v1/Secret
    NAME                 TYPE    DATA  AGE
    pouring-snail-mysql  Opaque  2     0s

    ==> v1/ConfigMap
    NAME                      DATA  AGE
    pouring-snail-mysql-test  1     0s

    ==> v1/PersistentVolumeClaim
    NAME                 STATUS   VOLUME  CAPACITY  ACCESS MODES  STORAGECLASS  AGE
    pouring-snail-mysql  Pending  0s

    ==> v1/Service
    NAME                 TYPE       CLUSTER-IP     EXTERNAL-IP  PORT(S)   AGE
    pouring-snail-mysql  ClusterIP  10.102.51.213  <none>       3306/TCP  0s

    ==> v1beta1/Deployment
    NAME                 DESIRED  CURRENT  UP-TO-DATE  AVAILABLE  AGE
    pouring-snail-mysql  1        1        1           0          0s

    ==> v1/Pod(related)
    NAME                                 READY  STATUS   RESTARTS  AGE
    pouring-snail-mysql-d6c855b9b-zdq5g  0/1    Pending  0         0s

    NOTES:
    MySQL can be accessed via port 3306 on the following DNS name from within your cluster:
    pouring-snail-mysql.default.svc.cluster.local

    To get your root password run:

        MYSQL_ROOT_PASSWORD=$(kubectl get secret --namespace default pouring-snail-mysql -o jsonpath="{.data.mysql-root-password}" | base64 --decode; echo)

    To connect to your database:

    1. Run an Ubuntu pod that you can use as a client:

        kubectl run -i --tty ubuntu --image=ubuntu:16.04 --restart=Never -- bash -il

    2. Install the mysql client:

        $ apt-get update && apt-get install mysql-client -y

    3. Connect using the mysql cli, then provide your password:
        $ mysql -h pouring-snail-mysql -p

    To connect to your database directly from outside the K8s cluster:
        MYSQL_HOST=127.0.0.1
        MYSQL_PORT=3306

        # Execute the following commands to route the connection:
        export POD_NAME=$(kubectl get pods --namespace default -l "app=pouring-snail-mysql" -o jsonpath="{.items[0].metadata.name}")
        kubectl port-forward $POD_NAME 3306:3306

        mysql -h ${MYSQL_HOST} -P${MYSQL_PORT} -u root -p${MYSQL_ROOT_PASSWORD}
    ```

To modify something about the configuration, definitions can be exported and applied again.

    sudo kubectl get services mysql-db -o yaml --export > mysql.yaml
    sudo kubectl apply -f mysql.yaml

In the example above, the stable/mysql chart was released, and the name of our new release is smiling-penguin. You get a simple idea of the features of this MySQL chart by running

    sudo helm inspect stable/mysql

Whenever you install a chart, a new release is created. So one chart can be installed multiple times into the same cluster. And each can be independently managed and upgraded.

Export the service as NodePort (**Ingress** is the way to export this to the outside world).

- Export the definition of the service already create and tweak the configuration (it can also edited directly)

        get services/mysql-db -o yaml > mysql-db-service.yaml

- Modify the files as follow

    ```yml
    apiVersion: v1
    kind: Service
    metadata:
      creationTimestamp: 2018-08-18T08:39:33Z
      labels:
       app: mysql-db
       chart: mysql-0.8.3
       heritage: Tiller
       release: mysql-db
      name: mysql-db-export
    spec:
      ports:
      - name: mysql
        port: 3306
        protocol: TCP
        targetPort: mysql
      selector:
        app: mysql-db
      sessionAffinity: None
    type: NodePort

    ```

- Get the services

        sudo kubectl get services

- Use the NodePort *3306:30046/TCP* obtained from *mysql-db-export service* in a MySQL client

    ```sql
    CREATE TABLE authors (id INT, name VARCHAR(20), email VARCHAR(20));

    INSERT INTO authors (id,name,email) VALUES(1,"Vivek","xuz@abc.com");
    INSERT INTO authors (id,name,email) VALUES(2,"Priya","p@gmail.com");
    INSERT INTO authors (id,name,email) VALUES(3,"Tom","tom@yahoo.com");

    SELECT * FROM authors
    ```

To uninstall a release.

    sudo helm list
    sudo helm delete <NAME>
    sudo helm del --purge <NAME>

To remove all releases

    helm delete $(helm list --short)

### Install MySQL (shared volume)











## Create custom Charts from exiting

Get the git repostory with allt the charts from GitHub

    git clone https://github.com/helm/charts.git

Modify the default values on the current folder/project **values.yaml**

    vi /charts/stable/prometheus/values.yaml

> For example you may want enable or disable some services or persistence volumes.

Update or install current version on the cluster

    helm install -f charts/stable/prometheus/values.yaml stable/prometheus

In order to accesos to the Chart externally, it recommend you to perform some operations and export some ports to the Network.

    export POD_NAME=$(kubectl get pods --namespace default -l "app=prometheus,component=pushgateway" -o jsonpath="{.items[0].metadata.name}")
    kubectl --namespace default port-forward $POD_NAME 9091

## References

- [Application Dashboard for Kubernetes](https://kubeapps.com/)
- [Official Helm Website](https://helm.sh/)
- [Quickstart Guide](https://docs.helm.sh/using_helm/#quickstart-guide)
- [How To install Helm](http://zero-to-jupyterhub.readthedocs.io/en/latest/setup-helm.html)