#!/bin/bash

echo 'Starting Provision: web'$1
apt-get update
apt-get install -y nginx
echo '<h1>Machine: web'$1'<h1>' >> /usr/share/nginx/html/index.html

echo 'Provision Complete' 