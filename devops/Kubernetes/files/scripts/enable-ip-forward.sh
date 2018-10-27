
# sudo cat  /etc/sysctl.conf
# Search for the parameters commented that enables ip-forwarding

# -- /etc/sysctl.conf --
# Uncomment the next line to enable packet forwarding for IPv4
#net.ipv4.ip_forward=1

sudo sed -i 's/#net.ipv4.ip_forward/net.ipv4.ip_forward/g' /etc/sysctl.conf

# Previous command menasThis means:
#    - search in file /etc/sysctl.conf
#    - Find/grep #net.ipv4.ip_forward
#    - Replace with net.ipv4.ip_forwardp
#    - Apply the changes in place -i

# Verify is uncommented
# sudo cat  /etc/sysctl.conf

# Uncomment the next line to enable packet forwarding for IPv4
# net.ipv4.ip_forward=1 ##UNCOMMENTED

# If not enabled after rastarting the vagrant machine doit manually
# sudo sysctl -p /etc/sysctl.conf

