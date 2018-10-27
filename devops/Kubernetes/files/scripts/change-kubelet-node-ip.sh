
# Get the IP address assigned to the second interface created by vagrant 'enp0s8'

sudo export NODE_IP=$(ifconfig enp0s8 | grep "inet addr" | cut -d ':' -f 2 | cut -d ' ' -f 1)

# Append the following string into the kubelet configuration --node-ip=<$NODE_ID>

sudo sed -i "$ s/$/--node-ip=$NODE_IP/" /etc/default/kubelet

# Verify the file has been changed accordingly
# sudo cat /etc/default/kubelet

#Restart Kubelet services (if not recognized at master)
# systemctl daemon-reload
# systemctl restart kubelet

