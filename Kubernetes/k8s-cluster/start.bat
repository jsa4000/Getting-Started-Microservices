ECHO "Start Kubernets cluster"

ECHO "Start K8s Master Server"
cd k8s-master
vagrant up
cd ..

ECHO "Start K8s Node 01 Server"
cd k8s-node01
vagrant up
cd ..

ECHO "Start K8s Node 02 Server"
cd k8s-node02
vagrant up
cd ..
