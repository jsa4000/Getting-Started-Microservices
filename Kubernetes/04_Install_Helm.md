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

## Example Chart (MySQL)

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

> This directories should be created on the storage server or node selected (**nodeSelector.diskType=ssd**) previously.

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

- Create a label onto the k8s-node2 to identify where the pods must be installed.

        sudo kubectl label nodes/k8s-node2 diskType=ssd
        sudo kubectl get nodes --show-labels

- Install the helm package providing the information *(**storageClassName**)* created on **pv-volumes.yaml**. This autmatically will claim for an available store, in this case the previous store created.

> Note in the command the parameter **nodeSelector.diskType=ssd** has been used to create the pods in the same node since the persistence resides in that node. In the next section shared storages will be seen for more advance and neat features.

    sudo helm install --name mysql-db --set nodeSelector.diskType=ssd,mysqlRootPassword=secretpassword,mysqlUser=admin,mysqlPassword=admin,mysqlDatabase=default-schema,persistence.storageClass=local stable/mysql

    ```txt
    NAME:   mysql-db
    LAST DEPLOYED: Sat Aug 18 13:31:26 2018
    NAMESPACE: default
    STATUS: DEPLOYED

    RESOURCES:
    ==> v1/Service
    NAME      TYPE       CLUSTER-IP      EXTERNAL-IP  PORT(S)   AGE
    mysql-db  ClusterIP  10.104.197.156  <none>       3306/TCP  0s

    ==> v1beta1/Deployment
    NAME      DESIRED  CURRENT  UP-TO-DATE  AVAILABLE  AGE
    mysql-db  1        1        1           0          0s

    ==> v1/Pod(related)
    NAME                       READY  STATUS    RESTARTS  AGE
    mysql-db-78cbb845d8-9kx7z  0/1    Init:0/1  0         0s

    ==> v1/Secret
    NAME      TYPE    DATA  AGE
    mysql-db  Opaque  2     0s

    ==> v1/ConfigMap
    NAME           DATA  AGE
    mysql-db-test  1     0s

    ==> v1/PersistentVolumeClaim
    NAME      STATUS  VOLUME    CAPACITY  ACCESS MODES  STORAGECLASS  AGE
    mysql-db  Bound   pv-00004  10Gi      RWO           local         0s


    NOTES:
    MySQL can be accessed via port 3306 on the following DNS name from within your cluster:
    mysql-db.default.svc.cluster.local

    To get your root password run:

        MYSQL_ROOT_PASSWORD=$(kubectl get secret --namespace default mysql-db -o jsonpath="{.data.mysql-root-password}" | base64 --decode; echo)

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

Verify the node has been installed on k8s-node2

     sudo kubectl get pods -o wide

```txt
NAME                        READY     STATUS    RESTARTS   AGE       IP           NODE        NOMINATED NODE
mysql-db-78cbb845d8-9kx7z   1/1       Running   0          21s       10.244.2.3   k8s-node2   <none>
```

To modify something about the configuration, definitions can be exported and applied again.

    sudo kubectl get services mysql-db -o yaml --export > mysql.yaml
    sudo kubectl apply -f mysql.yaml

In the example above, the stable/mysql chart was released, and the name of our new release is smiling-penguin. You get a simple idea of the features of this MySQL chart by running

    sudo helm inspect stable/mysql

Whenever you install a chart, a new release is created. So one chart can be installed multiple times into the same cluster. And each can be independently managed and upgraded.

Export the service as NodePort (**Ingress** is the way to export this to the outside world).

- Export the definition of the service already create and tweak the configuration (it can also edited directly)

        sudo kubectl sget services/mysql-db -o yaml > mysql-db-service.yaml

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

Verify (mysql) the deletion of the pod, so it will be restarted again using the volume and installed in the same node

    sudo kubectl delete pods/mysql-db-78cbb845d8-9kx7z

To uninstall a release.

    sudo helm list
    sudo helm delete <NAME>
    sudo helm del --purge <NAME>

In order to recover the persistance volumes, you may have to delete and recreate the PersistentVolume resource.

To remove all releases

    helm delete $(helm list --short)

### Install MySQL (shared volume)

In ordes to install packages with stateful state, it is needed to rely on shared storage capabilities. On bare metal installation, the easisest way is configuring a NFS server so it will be used among kubernetes cluster and pods.

- [Set Up an NFS Mount on Ubuntu 16.04](https://www.digitalocean.com/community/tutorials/how-to-set-up-an-nfs-mount-on-ubuntu-16-04)
- [kubernetes bare metal storage](https://medium.com/devityoself/kubernetes-bare-metal-storage-49b69d090dfa)
- [using persistent volumes on baremetal](https://docs.giantswarm.io/guides/using-persistent-volumes-on-baremetal/)
- [persistent storage nfs](https://docs.okd.io/latest/install_config/persistent_storage/persistent_storage_nfs.html)

To verify if NFS server is already installed, we use the following command (from k8s-master where the NFS server are going to be installed)

    dpkg -la | grep nfs

If there is no result from previous command then proceed to install the **server**.

    sudo apt-get update
    sudo apt-get install nfs-kernel-server -y

On **workers**, or any node have to access to NFS server, it must be installed the following package instead

    sudo apt install nfs-common -y

Create the folder where nfs data is stored. In this case we create *volumes* folder and the desired number of volumes with a fix name convencion *pvXXXXX*

    sudo mkdir -p /data/volumes
    sudo mkdir pv{001..010}

    # Transfer the owner to sudo to anybody (recursively)
    sudo chown -R nobody:nogroup /data
    sudo chmod -R 777 /data # Just for testing grant all persmissions

Add this folder into the shared folder for NFS server

    sudo vi /etc/exports

    ## Add following line to share the entire content
    /data *(rw,no_root_squash,no_subtree_check)

Restart the server

    sudo systemctl restart nfs-kernel-server

Create following **PersistanceVolume** operators configuring the **nfs server** and *storageClassName*

Crate a file in order to create the volumes assigned to the NFS folders created previously. *pv-nfs-volumes-001_010.yaml.yaml*

```yml
apiVersion: v1
kind: PersistentVolume
metadata:
  name: pv0001
spec:
  capacity:
    storage: 10Gi
  accessModes:
    - ReadWriteOnce
  persistentVolumeReclaimPolicy: Recycle
  storageClassName: nfs-slow
  nfs:
   path: /data/volumes/pv001
   server: 10.0.0.11
---
apiVersion: v1
kind: PersistentVolume
metadata:
  name: pv0002
spec:
  capacity:
    storage: 10Gi
  accessModes:
    - ReadWriteOnce
  persistentVolumeReclaimPolicy: Recycle
  storageClassName: nfs-slow
  nfs:
   path: /data/volumes/pv002
   server: 10.0.0.11
```

> **persistentVolumeReclaimPolicy** define the bahaviour of What happens to a persistent volume when released from its claim. See [Kubernetes API Documentation](https://kubernetes.io/docs/reference/generated/kubernetes-api/v1.11/#persistentvolumeclaim-v1-core) for further available options.

Create those volumes into Kubernetes cluster

    sudo kubectl apply -f /vagrant/files/volumes/pv-nfs-volumes-001_010.yaml

Install *stable/mysql* helm char using the *persistence.storageClass=nfs-slow* we have created and configured

    sudo helm install --name mysql-db --set mysqlRootPassword=secretpassword,mysqlUser=admin,mysqlPassword=admin,mysqlDatabase=default-schema,persistence.storageClass=nfs-slow stable/mysql

Check pv are already **bounded** to the PersistenceVolumeClaim

    sudo kubectl get pv -o wide

```txt
NAME      CAPACITY   ACCESS MODES   RECLAIM POLICY   STATUS      CLAIM              STORAGECLASS   REASON    AGE
pv0001    10Gi       RWO            Recycle          Available                      nfs-slow                 4m
...
pv0009    10Gi       RWO            Recycle          Available                      nfs-slow                 4m
pv0010    10Gi       RWO            Recycle          Bound       default/mysql-db   nfs-slow                 4m
```

Verify there is data into **NFS server** for **/data/volumes/pv010**

Check pvc are already **bounded** to the PersistenceVolume

    sudo kubectl get pvc -o wide

```txt
NAME       STATUS    VOLUME    CAPACITY   ACCESS MODES   STORAGECLASS   AGE
mysql-db   Bound     pv0010    10Gi       RWO            nfs-slow       1m
```

Check create pods are running

    sudo kubectl get pods -o wide

```txt
NAME                        READY     STATUS    RESTARTS   AGE       IP           NODE        NOMINATED NODE
mysql-db-67bd8bc5db-jlq47   1/1       Running   0          2m        10.244.2.2   k8s-node2   <none>
```

If the status if not equal to *running* check the logs by using these commands

    sudo kubectl logs pods/mysql-db-67bd8bc5db-jlq47
    sudo kubectl describe pods/mysql-db-67bd8bc5db-jlq47

Create the NodePort or Ingress controller to test the MYSQL installation (Check previous chapter for the changes to to).

    sudo kubectl get services/mysql-db -o yaml > mysql-db-service.yaml
    sudo kubectl apply -f mysql-db-service.yaml

Check if the service has been created

    sudo kubectl get services -o wide

Connect to MYSQl and create some tables and insert data.

Remove the pods in order to verify the Persistence is running accordingly.

    sudo kubectl delete pods/mysql-db-67bd8bc5db-jlq47

Check again MySQL database

### Mount NFS on Linux

First, go to a worker (i.e *k8s-node1*) to verify NFS server is visbile from outside.

In order to mount an external NFS server use the **mount** command

    mkdir -p /home/vagrant/tmp
    sudo mount -t nfs 10.0.0.11:/data/volumes/pv001 /home/vagrant/tmp

> Now */data/volumes/pv001* and */data/volumes/pv001* are in sync.

To list all the mounts performed on the system type

    mount

To unmount use the path used for mount the volume

    sudo umount /home/vagrant/tmp -l

### NFS client on Windows

Installing the NFS client for MS Windows

> Install *Services for NFS* using *Programs and Features*

Mounting the export from */etc/exports*

    mount \\10.0.0.11\data\ J:

To unmount all the nfs drives mounted type the following command

    umount -f -a

## Example Chart (Prometheus and Grafana)

In this section, it is going to be explained how to install **prometheus** and **grafana** charts using **Helm**

> We are going to use [Kubeapps Hub](https://hub.kubeapps.com/) to look for **prometheus** and **grafana** specifications and parameters. However it can be seen directly from [Github](https://github.com/helm/charts).

First, be sure having enough persistance volumes already provisioned (static). At least, it will be needed three of them.

    sudo kubectl get pv -o wide

    # If No resources found.
    sudo kubectl apply -f  /vagrant/files/volumes/pv-nfs-volumes-001_010.yaml

Install **prometheus** and **grafana** using the following commands. Be aware of the parameters passed through the helm.

    # Install stable/prometheus
    sudo helm install --name prometheus --namespace prometheus --set alertmanager.persistentVolume.storageClass=nfs-slow,server.persistentVolume.storageClass=nfs-slow,server.service.type=NodePort,server.service.nodePort=30001 stable/prometheus

    # Install stable/grafana
    sudo helm install --name grafana-dashboard --namespace grafana --set persistence.enabled=true,persistence.accessModes={ReadWriteOnce},persistence.size=8Gi,persistence.storageClassName=nfs-slow,service.type=NodePort stable/grafana

See the list of helm chart installed

    sudo helm list

```txtNAME                    REVISION        UPDATED                         STATUS          CHART                   APP VERSION     NAMESPACE
grafana-dashboard       1               Sun Aug 19 13:58:53 2018        DEPLOYED        grafana-1.14.0          5.2.2           grafana
prometheus              1               Sun Aug 19 13:53:04 2018        DEPLOYED        prometheus-7.0.2        2.3.2           prometheus
```

Check the status for pv, pvs and pods currently **binded** and **running**.

    sudo kubectl get pv -o wide
    sudo kubectl get pvc -o wide --all-namespaces
    sudo kubectl get pods -o wide --all-namespaces

Create the Ingress controller that binds both **grafana** and **prometheus** endpoints.

    sudo kubectl get svc -o wide -n=grafana
    sudo kubectl get svc -o wide -n=prometheus -l component=server

```txt
NAME                TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)   AGE       SELECTOR
prometheus-server   ClusterIP   10.107.151.168   <none>        80/TCP    15m       app=prometheus,component=server,release=prometheus
grafana-dashboard   ClusterIP   10.105.92.251    <none>        80/TCP    9m        app=grafana,release=grafana-dashboard
```

Install **ingress controller**

    sudo helm install stable/nginx-ingress --name nginx-ingress --set controller.stats.enabled=true,controller.metrics.enabled=true

Check the services that nginx-ingress controller has created

```yml
NAMESPACE     NAME                               TYPE           CLUSTER-IP       EXTERNAL-IP   PORT(S)                      AGE
default       kubernetes                         ClusterIP      10.96.0.1        <none>        443/TCP                      5h
default       nginx-ingress-controller           LoadBalancer   10.110.62.104    <pending>     80:30989/TCP,443:31158/TCP   4m
default       nginx-ingress-controller-metrics   ClusterIP      10.110.41.27     <none>        9913/TCP                     4m
default       nginx-ingress-controller-stats     ClusterIP      10.107.111.243   <none>        18080/TCP                    4m
default       nginx-ingress-default-backend      ClusterIP      10.97.106.240    <none>        80/TCP                       4m
```

On **cloud** environments the service *nginx-ingress-controller* (load balancer) creates a new ELB with elastic external-ip. However, on **bare-metal** environments, it is needed to create a service that exposes the nginx-ingress controller to the outside as a NodePort. Following file *ingress-nginx-service.yaml* exposes **app: nginx-ingress** to port **32700**

```yml
apiVersion: v1
kind: Service
metadata:
  name: ingress-nginx
  namespace: default
spec:
  type: NodePort
  ports:
  - name: http
    port: 80
    targetPort: 80
    protocol: TCP
  - name: https
    port: 443
    targetPort: 443
    protocol: TCP
  selector:
    app: nginx-ingress
```

    sudo kubectl apply -f /vagrant/files/ingress-controller/ingress-nginx-service.yaml

> This is the official [service](https://raw.githubusercontent.com/kubernetes/ingress-nginx/master/deploy/provider/baremetal/service-nodeport.yaml) with some changes in it.

Verify that there is something listening to that port from outside the server.

    curl http://10.0.0.11:32700/

Now lets create a **fan-out proxy** (or **reverse proxy**) to redirect the request over the different paths configured in ingress-controller. *monitoring-ingress*

```yml
apiVersion: extensions/v1beta1
kind: Ingress
metadata:
  annotations:
      kubernetes.io/ingress.class: nginx
  name: monitoring-ingress
  namespace: default
spec:
  rules:
  - host: monitoring.server.com
    http:
      paths:
      - path: /prometheus
        backend:
          serviceName: prometheus-server
          servicePort: 80
      - path: /grafana
        backend:
          serviceName: grafana-dashboard
          servicePort: 80
```

Create previous ingress service into kubernetes

    sudo kubectl apply -f /vagrant/files/monitoring/monitoring-ingress.yaml

Check current ingress controllers

    sudo kubectl get ingress
    sudo kubectl describe ingress/monitoring-ingress

Get the *admin* user password for grafana by running

    kubectl get secret --namespace grafana grafana-dashboard -o jsonpath="{.data.admin-password}" | base64 --decode ; echo

The internal **uri**  that is used to communicate between pods that belong to different namespaces is *service.namespace.svc.cluster.local*.

    sudo kubectl get svc --all-namespaces

```txt
NAMESPACE     NAME                            TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)           AGE
default       kubernetes                      ClusterIP   10.96.0.1        <none>        443/TCP           15m
grafana       grafana-dashboard               NodePort    10.108.41.170    <none>        30002:30635/TCP   1m
...
prometheus    prometheus-pushgateway          ClusterIP   10.99.66.164     <none>        9091/TCP          2m
prometheus    prometheus-server               NodePort    10.101.148.147   <none>        80:30001/TCP      2m
```

Since we want to use the local (clusterIP) of the service *prometheus-server* it must be used the Port 80 from  *80:30001/TCP*.

    prometheus-server.prometheus.svc.cluster.local:80

For **grafana** is must be configured following internal **url** address to access to Prometheus metrics:

    http://prometheus-server.prometheus.svc.cluster.local:80

Finally, sse following dashboards

- Node eXporter: 405
- Cadvisor: 893
- Kubernetes cluster monitoring: 1621

## Create custom Charts from reposirtory

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