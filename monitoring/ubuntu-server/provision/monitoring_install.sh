#!/usr/bin/env bash

echo "Download all the packages needed"
wget https://github.com/prometheus/prometheus/releases/download/v1.7.2/prometheus-1.7.2.linux-amd64.tar.gz
wget https://github.com/prometheus/node_exporter/releases/download/v0.14.0/node_exporter-0.14.0.linux-amd64.tar.gz
wget https://s3-us-west-2.amazonaws.com/grafana-releases/release/grafana-4.5.2.linux-x64.tar.gz
wget https://github.com/prometheus/alertmanager/releases/download/v0.8.0/alertmanager-0.8.0.linux-amd64.tar.gz

echo "Unzip all the packages"
tar -zxvf prometheus-1.7.2.linux-amd64.tar.gz
tar -zxvf node_exporter-0.14.0.linux-amd64.tar.gz
tar -zxvf grafana-4.5.2.linux-x64.tar.gz
tar -zxvf alertmanager-0.8.0.linux-amd64.tar.gz

echo "Copy Configuration"
cp /vagrant/config/prometheus.yml prometheus-1.7.2.linux-amd64/prometheus.yml




