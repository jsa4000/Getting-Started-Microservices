#!/usr/bin/env bash

echo "################################## Run nginx"
export DOLLAR='$'
export DOMAIN='domain.com'
envsubst < /vagrant/deployments/nginx/configs/nginx/nginx.conf.template > /etc/nginx/nginx.conf # /etc/nginx/conf.d/default.conf
nginx -g "daemon off;"


