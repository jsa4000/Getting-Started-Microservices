#!/usr/bin/env bash
echo "Creating Vagrant kubernets base image"

echo "- Load vagrantfile"

    vagrant up

echo "- Create the vagrant box from previous vagrant file"

    vagrant package --output k8s-image-base.box

echo "- Add the box to the image list"

    vagrant box add --name k8s-image-base k8s-image-base.box

echo "- Stop and destroy the box"

    vagrant destroy -f

