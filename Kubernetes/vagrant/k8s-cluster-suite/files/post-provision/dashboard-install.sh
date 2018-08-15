#File ,ust be in LF unix format to ingnore CR characters at the en of the line
echo "Installing kubernets dashboard"

kubectl apply -f /vagrant/files/dashboard-config/dashboard-no-auth.yaml
kubectl apply -f https://raw.githubusercontent.com/kubernetes/dashboard/master/src/deploy/recommended/kubernetes-dashboard.yaml

export K8S_NODE_IP=$(ifconfig enp0s8 | grep "inet addr" | cut -d ':' -f 2 | cut -d ' ' -f 1)
kubectl proxy --port=9999 --address=$K8S_NODE_IP --accept-hosts="^*$" &





