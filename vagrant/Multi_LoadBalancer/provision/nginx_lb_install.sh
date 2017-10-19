#!/bin/bash

echo 'Starting Provision Load Balancer'
apt-get update
apt-get install -y nginx
service nginx stop

echo 'Reconfiguring Nginx as Load Balancer'
rm -rf /etc/nginx/sites-enabled/default
touch /etc/nginx/sites-enabled/default
echo "
upstream testweb{
    server 10.0.0.11;
    server 10.0.0.12;
}

server {
    listen 80 default_server;
    listen [::]:80 default_server ipv6only=on;

    root /usr/share/nginx/html;
    index index.html index.htm;

    # Make accessible through http://localhost/
    server_name localhost;

    location / {
        proxy_pass http://testweb;
    }

}
" >> /etc/nginx/sites-enabled/default

echo 'Start again Nginx with the nex configuration'
service nginx start
echo 'Machine: lb1' >> /usr/share/nginx/html/index.html

echo 'Provision Complete' 