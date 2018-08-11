# Vagrant, Virtual Box and Ansible

## 1 Install Linux Server Manually

Install Virtual Machine Ubuntu server 16.04. 

> Virtual Box, Hyper-V, VMWare, etc..

####1.1 Create the New Virtual Machine Selection Linux and Ubuntu x64 distribution.

Use Following Settings:

System:

- Base Memory: 2048MB

General :

- Shared Clipboard: Bidireccional
- Drag'n Drop: Bidireccional

Network Settings (Adapter 1):

- Select Network Interface Device
- Attached To: Bridge Adapter
- Promiscuous Mode: Allow All
- MAC Address: Refresh to avoid same MAC

###1.2 Install VM from ISO

Run previous Vistual Machine create and Select desired ISO Linux distribution

Press F4-Mode and select *"Install a minimal virtual machine"* from the Start Menu.

Press *"Install Ubuntu Server"* and Follow the instructions.

Select Install Open SSH at the end of the installation process.

###1.3  Update main Packages

	$ sudo apt-get update
	$ sudo apt-get dist-upgrade or sudo apt-get upgrade -Y
	
###1.4 Create SSH key from client

This is done from client-side. Client will generate SSH private and public keys. 
Public key will be copied and shared to the server so the user won't need to use the pasword again.

- Generate private and public key (.pub) using *rsa* method (by defaul 2048 encryption level)

	$ ssh-keygen -t rsa
	
> Encryption level change changed using $ ssh-keygen -t rsa -b 4096

- Verify *.ssh* folder has been created in the desired folder and three files have feen created.

	$ ls -la  (list all/hidden folder & files)
	$ cd .ssh
	$ ls
	
To be sure the certificate will be installed correctly, the host must be added to the ".ssh/known_hosts" file:

	$  ssh-keygen -f "/home/jsa4000/.ssh/known_hosts" -R <host>
	
- Transfer Client key to host (Public Key) (Default Port number 22)

	$ ssh-copy-id <username>@<host> -p <portnumber>
	
	$ ssh-copy-id -i my.key.pub <username>@<host>
	
Password from Server user will be required to finish this operation.

- At the end of the process the SSH key will be installed on the Server

Finally, connect to the Server by using SSH
	
	$ ssh <username>@<host> -p <portnumber>
	
	
##2 Install Linux Server with Vagrant

Install Virtual Box and Vagrant from the current OS.

Test vagrant has been correctly installed:

	> vagrant -v

List all the Virtual Machine installed

	> vagrant box list
	
###2.1 Install Vagrant Boxes

> Search for *Vagrant boxes* to find available boxes. 
> Go to Official repository https://app.vagrantup.com/boxes/search

####2.1.1 Install Vagrant Boxes Automatically

To install automatically Vagrant Boxes use official hashicorp site. https://app.vagrantup.com/boxes/search

> Vagrant automatically will download the box if it isn't available in vagrant list boxes yet.

In order to proceed with the automatic installation, do the following Steps
  
- Create new folder where the Vagrant Project and main Vagrantfile will be created

	> md <project_name>
	> cd <project_name>
	> vagrant init
	
- Edit the Vagrantfile and set the box to be created (Search for Virtual Box Image):

	*Vagrantfile*

	Vagrant.configure("2") do |config|
	  config.vm.box = "ubuntu/xenial64"
	end

- Download and run the box onto Virtual Box.

	> vagrant up
 
 > Vagrant commands depend always in the vagrant folder and current vagrantfile
    
####2.1.2 Install Vagrant Boxes Manually

There is another way to add manually the box into Vagrant using the Add commmand.

To install a box just use the following steps:

	> vagrant box add <title> <url-box>
	> vagrant init <title>
	> vagrant up
	
To remove a box image from list just use

	> vagrant box remove <title>
	
> To see all available commands use *vagrant box*

Following an example that manually adds a Box into Vagrant:

- Got to http://www.vagrantbox.es/ to find a desired Box to install

	Ubuntu 16.04 with Docker enabled (based on amd64 server iso file)
	VirtualBox 	
	https://github.com/jose-lpa/packer-ubuntu_lts/releases/download/v3.1/ubuntu-16.04.box
	567 MB

- Add the current box to Vagrant (This will donwload the current Image autmatically)
	
	> vagrant box add ubuntu-amd64 https://github.com/jose-lpa/packer-ubuntu_lts/releases/download/v3.1/ubuntu-16.04.box	

- Also boxes can be included manually into Vagrantfile

	Vagrant.configure("2") do |config|
		config.vm.box = "ubuntu-amd64"
		config.vm.box_url = "https://github.com/jose-lpa/packer-ubuntu_lts/releases/download/v3.1/ubuntu-16.04.box" 
	end
	
- Verify the box has been added to Vagrant 

	> vagrant box list
	
###2.2 Configure Vagrant Boxes

Create a new directoy with the project for the current Virtual Machine to be created

	> md vagrant-ubuntu-amd64
	> cd vagrant-ubuntu-amd64
	
Initialize the virtual machine and create a project and the Vagrantfile configuration file.

	> vagrant init <default_vagrant_box>
	
> You can create vagrantfile by using a default box. This will add the box to *config.vm.box*
	
Previous action will create the main file *Vagrantfile* and a *.vagrant* folder.

> Create file contains examples to configure a Vagrant box.

*Vargantfile* (example)

	Vagrant.configure("2") do |config|
	  config.vm.box = "ubuntu-amd64"
	  config.vm.hostname = "ubuntu-amd64-master"
	  config.vm.network "private_network", ip: "192.168.1.190"
	  config.vm.synced_folder ".", "/vagrant", type: "nfs"
	  config.vm.provider "virtualbox" do |vb|
		 vb.gui = true
		 vb.name = "Vagrant-ubuntu-amd64"
		 vb.customize ["modifyvm", :id, "--cpuexecutioncap", "50", "--cpus", "1"]
		 vb.memory = "1024"
	   end
	  config.vm.provision :shell, path: "shell-ubuntu-16.04.v1.sh"
	end

> Vagrant also support standard shortcuts for providers instead using commands via *vb.customize*.

	vb.customize ["modifyvm", :id, "--cpus", "1"] 
	vb.cpus = 2

*shell-ubuntu-16.04.v1.sh *

	#!/usr/bin/env bash

	# Intended for Ubuntu 16.04 (ubuntu-amd64)
	echo "-- Start Vagrant Install Shell  --"
	
	# Update Ubuntu
	echo "-- Installing Updates  --"
	apt-get update
	apt-get dist-upgrade
	
	echo "-- End Vagrant Install Shell  --"

> Writing Bash files using MS Windows could derive into some problems due UNIX format and CLRF .	
	
If any changes are done to this file and the Vagrant Box is running just update the provision:

	>vagrant provision	
	
	
###2.2 Run Vagrant Boxes

Run the current Vagrantfile located current folder. This will create the image into Virtual Box

	> vagrant up
	
> First time vagrant will run all provisions set and download the box if neccesary. 
> This can be forced by using the parameter:  --[no-]provision - Force, or prevent, the provisioners to run.
	
	> vagrant up --provision
	
Log-in into the Virtual Machine. Use following command from current Vagrant project:
	
	> vagrant ssh
	
Or connect from a bash console using normal SSH command in Windows/Linux
	
	> ssh <user>@<host>

> Default user and Password for Vagrant boxes are *vagrant/vagrant*
> If vagrant is not running on Ms Windows 7. Install Power Shell Tools 5.1 of higher

###2.3 Shutdown/hybernate Vagrant Boxes

Vagrant images are state-less so every time the box is started the state will be the same after the provisioning.
Suspended vagrant images enter into an Hybernate state, so all changes are maintained after *resume*.

The provision phase can be forced to be run, so Vagrant will execute again the scripts in Vagrantfile or specifics x,y,z...

To *Shutdown* a Vagrant Box use the following command:

	> vagrant halt
	
To re-start again the box use:

	> vagrant up
		
To run the commands *halt* and *up* at the same time, use following command to force reload the image:

	> vagrant reload
	
> It can be seen the current status using Visutal Box tool manager

To *hybernate* and resume a vagrant box from *suspend* state use:

	> vagrant suspend
	> vagrant status
	> vagrant resume
	> vagrant status
	
To remove the current box (VM) created (using *vagrant up*) use:

	> vagrant destroy <name>

###2.4 Create New Vagrant Boxes

In order to create new Vagrant box execute the following command:

	> vagrant package --output <outputfile.box>
	
> It's recommended to do a maintenance to clean Linux cache and temp files to shorten de pacakage box file.

After creating the pacakge, it's needed to add this box to Vagrant in order to use it.

	> vagrant box add <name > <outputfile.box>
	> vagrant box add <name > <outputfile.box>
	> vagrant box list
	
To delete a box from the list use detroy

	> vagrant destroy

###2.5 Create Snapshots from Vagrant Boxes

Vagrant allows to create current Snapshots of current VM state running

	> vagrant snapshot save "First snapshot"
	
> Vagrant will perform this operation in the proper box, depending on the current VAgrantFile in folder

To see all available snapshots for current vagrantfile run

	> vagrant snapshot list
	
To restore a snapshot, first run the vagrant box and then restore the snapshot

	> vagrant up
	> vagrant snapshot restore "First snapshot" --no-provision
	
> With --no-provision the shells won't be executed from Vagrantfile.

###2.6 Create Multiple-Vagrant Vistual machines

Vagrant support to define more that one configuration per Vagrantfile. 

This is done by using different configurations in the main vagrant definition:

*Vagrantfile*
	
	Vagrant.configure("2") do |config|
	 
	  config.vm.define "lb1" do |lb1|
		lb1.vm.box = "ubuntu/trusty64"
		lb1.vm.hostname = "lb1"
		lb1.vm.network "private_network", ip: "10.0.0.10"
		lb1.vm.provision "shell", path: "provision/nginx_lb_install.sh"
	  end
	  
	  config.vm.define "web1" do |web1|
		web1.vm.box = "ubuntu/trusty64"
		web1.vm.hostname = "web1"
		web1.vm.network "private_network", ip: "10.0.0.11"
		web1.vm.provision "shell" do |shell|
		  shell.args = "1"
		  shell.path = "provision/nginx_web_install.sh"
		end
	  end

	  config.vm.define "web2" do |web1|
		web1.vm.box = "ubuntu/trusty64"
		web1.vm.hostname = "web2"
		web1.vm.network "private_network", ip: "10.0.0.12"
		web1.vm.provision "shell" do |shell|
		  shell.args = "2"
		  shell.path = "provision/nginx_web_install.sh"
		end
	  end

	end

*provision/nginx_lb_install.sh*
	
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
	
*provision/nginx_web_install.sh*	

	#!/bin/bash

	echo 'Starting Provision: web'$1
	apt-get update
	apt-get install -y nginx
	echo '<h1>Machine: web'$1'<h1>' >> /usr/share/nginx/html/index.html

	echo 'Provision Complete' 
	
##3 Using Ansible With Vagrant

Ansible is an open-source automation engine that automates software provisioning, configuration management, and application deployment.

Ansible could be described as a Manager Configuration tool. There are other tools such as Chef, Puppet, Ansible, etc.. that have been developed to perform and automate the process of deploying and provisioning new Servers and Virtual Machines. These tools allow to easily deploy a large scale number of servers.

Vagrant also is a Manager Configuration tool but it cannot be distributed or scaled in a huge System. For this reason Ansible and these other tools ahs been developed.

https://www.atlantic.net/community/howto/getting-started-with-ansible/

###3.1 Install Ansible

The installation of Ansible is straigh-foward. The commands are the following:

    $ sudo apt-get install software-properties-common
    $ sudo apt-add-repository ppa:ansible/ansible
    $ sudo apt-get update
    $ sudo apt-get install ansible
	
###3.2 Configuring SSH with Ansible

Ansible needs to be able to connect to the servers via ssh that are included inside the inventory.
SSH keys are used to establish connection between the servers. In this case, the public key generated is copied/added into the servers that are going to be previsioned.

- Connect to the control where ansible is installed

	> vagrant ssh ansible

- Create a public and private ssh key.

	$ ssh-keygen -t rsa

> Note the path where the two files (private and public) have been created.
> Also creates a file called authorized_keys and known_hosts
	
- Add each host to the known_hosts:

	$  ssh-keygen -f ".ssh/known_hosts" -R <host>
	
- To set up SSH agent to avoid retyping passwords, use the following commands:

	$ ssh-agent bash
	$ ssh-add ~/.ssh/id_rsa or $ ssh-add ~/.ssh/<privatekey>
	
- copy the public-key to the desired Servers

	$ ssh-copy-id <user>@<host>
	$ ssh-copy-id -i y.pub vagrant@10.0.0.22

- Try to connect to the servers

	$ ssh <user>@<host>
	$ ssh -i key.pub <user>@<host>
	
> Be sure the seconds command is not needed so it works in Ansible
	
- Finally, test if Ansible connect to the host in inventory using ssh.

	$ ansible -i /ansible/inventory loadbalancer -m ping
	$ ansible -i /ansible/inventory all -m ping

###3.3 Create Ansible with multiple-nodes using Vagrant

This section will provide the scripts neccesary to deply some virtual machine and a Control manager using Ansible.

- Create the Vagrantfile with the definition for the instances to create.

*Vagrantfile*

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
			end
		end
	end

- Create the neccesary script to create the Control Manager installing Ansible

*provision/ansible_install.sh*

	#!/usr/bin/env bash

	echo "- Installing Ansible"
	apt-get -y install software-proerties-common
	apt-add-repository -y ppa:ansible/ansible
	apt-get update
	apt-get -y install ansible
	echo "- Ansible Installed"


In this case it's not neccesary to create or install any other provisioning since this will be managed by playbook using Ansible.
For using Ansible it's neccesary to create configuration whit the definition of the Machines that wil l be managed.

###3.4 Create Ansible Inventory (hosts)

In order to create the inventory it could be used two methods.

The first methods it to create a plain-text/yml based wiht all the hosts. The host can be grouped into categories.
This methods will require to configure ssh in the control machine server to connect to the other hosts. 
In order to do this it would be needed to create ssh keys and share between the different servers. 

*inventory*

	[loadbalanacer]
	10.0.0.11
	#10.0.0.12
	#10.0.0.13

	[node]
	10.0.0.21
	10.0.0.22
	#10.0.0.21
	#10.0.0.22
	
	[databases]
	db-[a:f].example.com
	
> It could be used patterns to define hosts
	
*inventory.yml*

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

The other way is to create another plain-text/yml file specifying the hosts with user/password for ssh.
Using this methods is not required to create ssh keys since the userpassword will be used for the connection. 
However it's neccesary to connect to remote computer almost one time to accept the ssh connection.

*inventory*
	[loadbalanacer]
	10.0.0.11 ansible_connection=ssh ansible_ssh_user=vagrant ansible_ssh_pass=vagrant

	[node]
	10.0.0.21 ansible_connection=ssh ansible_ssh_user=vagrant ansible_ssh_pass=vagrant
	10.0.0.22 ansible_connection=ssh ansible_ssh_user=vagrant ansible_ssh_pass=vagrant

	
- Test the connections between the control manager and the host performing a ping operation

	$ ansible -i /ansible/inventory loadbalancer -m ping
	$ ansible -i /ansible/inventory all -m ping 


###3.5 Create Ansible PLaybook

the way Ansible define the Tasks por provisions to perform is using *playbooks*.
A playbook is an yml file which a sequence of task are defined. The task will be performed in the host selected.

*playbook.yml*

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

To exexute a playbook use the following command

	$ ansible-playbook -i /ansible/inventory playbook.yml 
	
###3.6 Use Ansible roles




###3.7 Use Ansible as Provisioner

The Vagrant Ansible Local provisioner allows you to provision the guest using Ansible playbooks by executing ansible-playbook directly on the guest machine.
Basically, Ansible engine is used instead of using *shell* provisioning. e.j. using scripts to define the task or actions to perform.

*Vagrantfile*

	Vagrant.configure("2") do |config|
	  # Run Ansible from the Vagrant VM
	  config.vm.provision "ansible_local" do |ansible|
		ansible.playbook = "playbook.yml"
	  end
	end



