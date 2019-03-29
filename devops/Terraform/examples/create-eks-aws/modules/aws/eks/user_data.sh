#!/usr/bin/env bash

sudo sed -i '/"nofile": {/,/}/d' /etc/docker/daemon.json
sudo sed -i '/OPTIONS/d' /etc/sysconfig/docker 
sudo systemctl restart docker

${user_data}
