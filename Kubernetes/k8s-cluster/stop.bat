ECHO "Stop Kubernets cluster"

ECHO "Stop K8s Master Server"
cd k8s-master
vagrant halt
cd ..

ECHO "Stop K8s Node 01 Server"
cd k8s-node01
vagrant halt
cd ..

ECHO "Stop K8s Node 02 Server"
cd k8s-node02
vagrant halt
cd ..
