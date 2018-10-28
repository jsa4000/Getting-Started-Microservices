
# -- /etc/ssh/sshd_config --
# Re-enable passwd authentication in vargrant to allow ssh-copy-id
# PasswordAuthentication no

sudo sed -i 's/PasswordAuthentication no/PasswordAuthentication yes/g' /etc/ssh/sshd_config
service sshd restart

