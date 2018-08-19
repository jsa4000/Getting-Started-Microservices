#!/usr/bin/env bash
echo "- Install NFS Server"

echo "-    Installing NFS Server packages"
apt-get install nfs-kernel-server -y

echo "-    Creating NFS Server structure at /data"
mkdir -p /data/volumes
cd /data/volumes
mkdir pv{001..100}

# Transfer the owner to sudo to anybody (recursively)
chown -R nobody:nogroup /data
# Just for testing grant all persmissions to all folders
chmod -R 777 /data 

# Add this folder into the shared folder for NFS server
echo '/data *(rw,no_root_squash,no_subtree_check)' >> /etc/exports

# Restart NFS server to apply new configuration
systemctl restart nfs-kernel-server