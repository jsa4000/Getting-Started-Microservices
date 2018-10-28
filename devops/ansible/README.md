# Ansible

## Configuration Management

**Configuration management** (CM) focuses on establishing and maintaining **consistency** of a product's performance, and its functional and physical attributes with its requirements, design, and operational information throughout its life. CM streamlines the delivery of software and applications by **automating** the build out of **systems** quickly and efficiently. It can be used by management and engineers to check which components have been **changed** and why, ensuring an audit trail of changes done to the system. This helps with quickly identifying bad configuration changes and allows for rollbacks to well-known working ones to ensure rapid restoration of service(s). This also helps developers with debugging to check if configuration changes impacts the product’s functionality.

CM does take time to setup, but if done correctly, it allows for ease of scalability and reduces the time to build out additional resources for your product without the worry of user-prone errors. CM may seem at sometimes daunting to setup and implement, but coming up with a strategy and starting out small can open the door of opportunities to remove the human abstraction layer and automate as much as possible. This allows you to really focus on making your product or service better for your customers instead of misspending time, money, and resources on maintaining your system infrastructure.

## Ansible

**Ansible** is an open-source automation engine that automates software provisioning, configuration management, and application deployment.

Ansible could be described as a **Manager Configuration tool**. There are other tools such as **Chef**, **Puppet**, **Ansible**, etc.. that have been developed to perform and automate the process of deploying and provisioning new Servers and Virtual Machines. These tools allow to easily deploy a large scale number of servers.

Vagrant also is a Manager Configuration tool but it cannot be distributed or scaled in a huge System. For this reason Ansible and these other tools ahs been developed.

[Getting started with Ansible](https://www.atlantic.net/community/howto/getting-started-with-ansible/)

### Install Ansible

The installation of Ansible is very straight-forward. The commands are the following:

    sudo apt-get install software-properties-common
    sudo apt-add-repository ppa:ansible/ansible
    sudo apt-get update
    sudo apt-get install ansible

> Check there are **no** dependencies **issues** depending on linux distributions. i.e. Check this [link](https://www.josharcher.uk/code/ansible-python-connection-failure-ubuntu-server-1604/) for Ubuntu 16.04 reference.

### Configuring SSH with Ansible

Ansible needs to be able to connect to the servers via ssh that are included inside the inventory.

SSH keys are used to establish connection between the servers. In this case, the public key generated is copied/added into the servers that are going to be provisioned.

- Connect to the control where ansible is installed

        vagrant ssh ansible

- Create a public and private ssh keys using ``ssh-keygen``. *Press enter until complete*.

        ssh-keygen -t rsa -b 4096

> Two new files (private and public) have been created. Also creates a file called authorized_keys and known_hosts

- copy the public-key to the desired Servers ``ssh-copy-id <user>@<host>``

        ssh-copy-id vagrant@10.0.0.11
        ssh-copy-id vagrant@10.0.0.21
        ssh-copy-id vagrant@10.0.0.22

> **Password** for vagrant images is ``vagrant``

- Try to connect to the servers using ``ssh <user>@<host>`` or ``ssh -i rsa_key.pub <user>@<host>``

        ssh vagrant@10.0.0.11

> Be sure the seconds command is not needed so it works in Ansible

- Finally, test if Ansible connect to the host in inventory using ssh.

        ansible -i /ansible/hosts loadbalancer -m ping
        ansible -i /ansible/hosts all -m ping

### Create Ansible with multiple-nodes using Vagrant

This section will provide the scripts necessary to deploy some virtual machine and a Control manager using Ansible.

- Create the Vagrantfile with the definition for the instances to create.

        ```ruby
        # Defines our Vagrant environment
        #
        # -*- mode: ruby -*-
        # vi: set ft=ruby :

        Vagrant.configure("2") do |config|

            # Create Ansible Control Management Node
            config.vm.define :ansible do |aconfig|
                aconfig.vm.box = "ubuntu/trusty64"
                aconfig.vm.hostname = "ansiblectl"
                aconfig.vm.network :private_network, ip: "10.0.0.10"
                aconfig.vm.synced_folder "ansible", "/ansible", type: "nfs"
                aconfig.vm.provider "virtualbox" do |vb|
                    vb.memory = 256
                end
                aconfig.vm.provision :shell, path: "provision/ansible_install.sh"
            end

            # Create Load Balancer
            config.vm.define :loadbalancer do |lbconfig|
                lbconfig.vm.box = "ubuntu/trusty64"
                lbconfig.vm.hostname = "loadbalancer"
                lbconfig.vm.network :private_network, ip: "10.0.0.11"
                lbconfig.vm.network "forwarded_port", guest: 80, host: 8080
                lbconfig.vm.provider "virtualbox" do |vb|
                    vb.memory = 256
                end
                lbconfig.vm.provision :shell, path: "provision/python_install.sh"
                lbconfig.vm.provision :shell, path: "provision/enable_auth.sh"
            end

            # Create Web Servers Nodes
            (1..2).each do |i|
                config.vm.define "node#{i}" do |nconfig|
                    nconfig.vm.box = "ubuntu/trusty64"
                    nconfig.vm.hostname = "node#{i}"
                    nconfig.vm.network :private_network, ip: "10.0.0.2#{i}"
                    nconfig.vm.network "forwarded_port", guest: 80, host: "808#{i}"
                    nconfig.vm.provider "virtualbox" do |vb|
                        vb.memory = 256
                    end
                    nconfig.vm.provision :shell, path: "provision/python_install.sh"
                    nconfig.vm.provision :shell, path: "provision/enable_auth.sh"
                end
            end
        end
        ```

- Create the necessary script ``provision/ansible_install.sh`` to create the Control Manager installing Ansible

        ```bash
        #!/usr/bin/env bash

        echo "Install Ansible for Management and Configuration"
        apt-get update
        apt-get -y install software-properties-common
        apt-add-repository ppa:ansible/ansible
        apt-get update
        apt-get -y install ansible
        ```

In this case it's not necessary to create or install any other provisioning since this will be managed by playbook using Ansible.

For using Ansible it's necessary to create configuration whit the definition of the Machines that wil l be managed.

### Create Ansible Inventory (hosts)

In order to create the inventory it could be used two methods.

The first methods it to create a plain-text/yml based wiht all the hosts. The host can be grouped into categories.

This methods will require to configure ssh in the control machine server to connect to the other hosts.

In order to do this it would be needed to create ssh keys and share between the different servers.

hosts

```txt
[loadbalanacer]
10.0.0.11

[node]
10.0.0.21
10.0.0.22

[databases]
db-[a:f].example.com
```

> It also could be used **patterns** to define hosts ``db-[a:f].example.com``

hosts.yml

```yaml
all:
    hosts:
    mail.example.com
    children:
    webservers:
        hosts:
        foo.example.com:
        bar.example.com:
    dbservers:
        hosts:
        one.example.com:
        two.example.com:
        three.example.com:
```

The other way is to create another ``plain-text/yml`` file specifying the hosts with user/password for ssh.

Using this methods is not required to create ssh keys since the user and password will be used for the connection.

However it's necessary to connect to remote computer almost one time to accept the ssh connection.

hosts

```txt
[loadbalanacer]
10.0.0.11 ansible_connection=ssh ansible_ssh_user=vagrant ansible_ssh_pass=vagrant

[node]
10.0.0.21 ansible_connection=ssh ansible_ssh_user=vagrant ansible_ssh_pass=vagrant
10.0.0.22 ansible_connection=ssh ansible_ssh_user=vagrant ansible_ssh_pass=vagrant
```

- Test the connections between the control manager and the host performing a ping operation

        ansible -i /ansible/inventory loadbalancer -m ping
        ansible -i /ansible/inventory all -m ping

### Create Ansible playbook

the way Ansible define the Tasks por provisions to perform is using *playbooks*.
A playbook is an yml file which a sequence of task are defined. The task will be performed in the host selected.

playbook.yml

```yaml
---
- hosts: loadbalanacer
  remote_user: vagrant
  become: yes
  tasks:
  - name: Run apt-get update
    apt: update_cache=yes

  - name: Update all packages to the latest version
    apt: upgrade=dist

  - name: Install nginx
    apt: name=nginx state=latest update_cache=true
    notify:
    - start nginx

  - name: Deactivate the default nginx site
    file: path=/etc/nginx/sites-enabled/default state=absent

  - name: Copy the nginx configuration file
    copy: src=/ansible/nginx/default dest=/etc/nginx/sites-enabled/default

  handlers:
  - name: start nginx
    service: name=nginx state=started

- hosts: node
  remote_user: vagrant
  become: yes
  tasks:
  - name: Run apt-get update
    apt: update_cache=yes

  - name: Update all packages to the latest version
    apt: upgrade=dist

  - name: Install nginx
    apt: name=nginx state=latest update_cache=true
    notify:
    - start nginx

  handlers:
  - name: start nginx
    service: name=nginx state=started
```

To execute a playbook use the following command

    ansible-playbook -i /ansible/inventory playbook_tasks.yml

### Use Ansible roles

Ansible allows to create roles. Roles may have specific configuration by default.

In order to execute ``roles`` instead of task, ansible provides the keyword roles, that executes the playbooks within the folders for each role

```yaml
---
- hosts: loadbalancer
  remote_user: vagrant
  become: yes
  roles:
  - update
  - loadbalancer

- hosts: node
  remote_user: vagrant
  become: yes
  roles:
  - update
  - nginx
```

Ansible will execute the following folder/playbooks for the hosts ``loadbalancer``:

- /roles/update/defaults/main.yml
- /roles/update/tasks/main.yml
- /roles/update/handlers/main.yml       // it is needed only if there is any notify event on previous playbooks.
- /roles/loadbalancer/defaults/main.yml
- /roles/loadbalancer/tasks/main.yml
- /roles/loadbalancer/handlers/main.yml

> It can be mixed **tasks** and **roles** in playbooks

To execute a playbook use the following command

    ansible-playbook -i /ansible/inventory playbook_roles.yml

### Use Ansible as Vagrant provisioner

The Vagrant Ansible Local provisioner allows you to provision the guest using Ansible playbooks by executing ansible-playbook directly on the guest machine.
Basically, Ansible engine is used instead of using *shell* provisioning. e.j. using scripts to define the task or actions to perform.

```ruby
Vagrant.configure("2") do |config|
    # Run Ansible from the Vagrant VM
    config.vm.provision "ansible_local" do |ansible|
    ansible.playbook = "playbook.yml"
    end
end
```
