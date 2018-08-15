#!/usr/bin/env bash

#Install all features
bash /vagrant/post-provision/kubetctl-init.sh
bash /vagrant/post-provision/helm-install.sh
bash /vagrant/post-provision/dashboard-install.sh