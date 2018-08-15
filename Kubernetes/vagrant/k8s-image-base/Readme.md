# Creating Vagrant Image

- Load the vagrantfile normally

    vagrant up

- Create the vagrant box from previous vagrant file (this command stops the current instance)

    vagrant package --output k8s-image-base.box

- Add the box to the image list.

    vagrant box add --name k8s-image-base k8s-image-base.box

- Check the image has been correctly added into local vagrant repository

    vagrant box list

- Stop and destroy the box

    vagrant destroy -f
