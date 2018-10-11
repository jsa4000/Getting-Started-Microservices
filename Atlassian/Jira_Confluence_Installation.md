# jira And Confluence Installation

## 1. Creating Virtual Machine

The idea is to create a Virtual Machine using Vagrant with the following packadges

- Docker Engine (Docker Community Edition CE)
- Docker compose v1.16.1
	
> To install Docker Compose it's neccessary to include the version to install. It can be seen at https://github.com/docker/compose/releases

### 1.1 Vagrantfile

If Hosting is managed by the Team instead using Cloud Virtual Hosting, it's recommended to use Vagrant or Ansible Configuration Managers to deploy and manage the IT.

The process to create a Virtual Machine using Vagrant with the previous requisites is the following:

1. Create Vagrant directory to create the configuration for the Virtual Machine to deploy

	> md UbuntuMaster
	> cd UbuntuMaster
	
2. Initialize VagrantFile using a default Vagrant box  from the Offical repository:

			UbuntuMaster/ > vagrant init ubuntu/xenial64
	
3. Now let's modify *VagrantFile* with the desired configuration

	Vagrant.configure("2") do |config|
	  config.vm.box = "ubuntu/xenial64"
	  config.vm.hostname = "ubuntu-master-xenial64"
	  config.vm.network "private_network", ip: "10.0.0.10"
	  config.vm.synced_folder ".", "/vagrant"
	  config.vm.provider "virtualbox" do |vb|
		 vb.gui = true
		 vb.name = "Vagrant-Ubuntu-Master-Xenial64"
		 vb.memory = "2048"
	  end
	  config.vm.provision :shell, path: "provision/ubuntu_update.sh"
	  config.vm.provision :shell, path: "provision/docker_install.sh"
	  config.vm.provision :shell, path: "provision/docker_compose_install.sh"
	end
	
4. run and Test the Vagrant Box

	UbuntuMaster/ > vagrant up
	UbuntuMaster/ > vagrant ssh
	
5. To destroy and delete the Box

	UbuntuMaster/ > vagrant destroy
	
Also it can be created snapshots from the current virtual machine state. To create a snapshot to the end (push) of the stack use the following command.

	UbuntuMaster/ > vagrant snapshot push
	
> It can be created also in-between snapshots to create a hierarchy from original the original Box.

Shutdown the virtual machine.
	
	UbuntuMaster/ > vagrant halt
		
To restore the latest (pop) snapshot from a Vagrantfile.

	UbuntuMaster/ > vagrant snapshot pop
	
vagrant snapshot push

### 1.2 Update Linux Script

This script is the typical task performed after installing a new Linux distribution:

*provision/ubuntu_update.sh*

	#!/usr/bin/env bash
	echo "- Updating Linux Distribution"
	apt-get update -y
	apt-get upgrade -y
	
### 1.3 Docker installation Script

This script install the latest Docker CE distribution available. In production it's recommended to install a particular version to avoid possible errors.

*provision/docker_install.sh*

	#!/usr/bin/env bash
	echo "- Installing Docker"
	apt-get update -y
	apt-get install \
		apt-transport-https \
		ca-certificates \
		curl \
		software-properties-common
	curl -fsSL https://download.docker.com/linux/ubuntu/gpg | apt-key add -
	add-apt-repository \
	   "deb [arch=amd64] https://download.docker.com/linux/ubuntu \
	   $(lsb_release -cs) \
	   stable"
	apt-get update -y
	apt-get install docker-ce -y

In order to verify docker has been succesfully installed, login into the box and execute the following command.

	$ sudo docker run hello-world
	
Or simply get the current docker version isntalled

	$ sudo docker --version
	
### 1.4 Docker Compose installation Script

This script install Docker compose into the current Machine. In this case the version needs to be specified when downloading.

https://github.com/docker/compose/releases/download/1.16.1/docker-compose-Linux-x86_64

*docker_compose_install.sh*

	#!/usr/bin/env bash
	echo "- Installing Docker Compose"
	curl -L https://github.com/docker/compose/releases/download/1.16.1/docker-compose-`uname -s`-`uname -m` -o /usr/local/bin/docker-compose
	chmod +x /usr/local/bin/docker-compose

> Official documentation uses a redirection for *curl* command instead '-o'. Sometimes this way doesn't work, depending on the priviledges of the user.
	
To verify docker installation execute the following command:

	$ sudo docker-compose --version


## 2 Creating Docker Compose File

The next step is to create a docker-compose.yml file that will be used to deploy the desired docker containers.

The docker compose file will download and run the following docker images:

- Jira: cptactionhank/atlassian-jira:latest
- Confluence: cptactionhank/atlassian-confluence:latest
- Postgress: postgres:latest


### Dockerfile vs docker-compose.yml 

A *Dockerfile* is the definition of a docker *image*. An image is created with this file an using the *build* command. In this file, it must be specified the base image and other dependences to install or to configure inside the container.

A Docker *container* is created using run command from a particular *image*. It's lika an instance of an image and it can be created multiple containers using the same image. For each container, additional configuration can be defined or overriding from original image such as: Port, volumes, environment variables, commands, links, etc..

Docker compose manage the creation of multiple containers using a yml file, called *docker-compose.yml*. It's very similar to the run commands but it's defined using yml standard. Also it can be configured to deply multiple containers. Theses containers (services) also are connected between them, so it's not required to use links between the containers defined. Also it can be set another contraintes such as *dependences* between the containers, that define the order in which are created and another features for *restart* mode, etc.

### 2.1 Create Jira Docker container 

The idea is to create volumes inside the docker container to be able to backup and maintain the latest state of jira and database.

*docker-compose.yml*

	version: '3'
	services:
	  jira:
		image: cptactionhank/atlassian-jira:latest
		restart: always
		ports:
		  - '80:8080'
		links:
		  - database
		volumes:
		  - jira-data:/var/atlassian/jira

	  database:
		image: postgres:latest
		restart: always
		volumes:
		  - database-data:/var/lib/postgresql/data

	volumes:
	  jira-data:
	  database-data:
	  
> Links to other containers are automatically added by docker-compose using the service name, but can be specified manually inside the list "links:".
	  
In order to execute the current docker container. It's neccessary to create new directory and copy the desired docker-compose.yml file to deploy.
 
	$ mkdir jira
	$ sudo cp /vagrant/deployments/jira/docker-compose.yml jira/

Finally execute the current docker-compose file
	
	$ sudo docker-compose up
	
> It can be run also in detached mode (Backgrpund mode): sudo docker-compose -d up
	
Verify the installation using the forwarded port 80 (from port 8080)

	http://10.0.0.10
	
To Stops the docker compose entirely use the following command

	$ sudo docker-compose down
	
> To enter into a running docker container use the command: 

	sudo docker exec -it deployment_nginx_1 bash
	
### 2.2 Create Confluence-Jira Docker container

Following is the structure of the system to create (without Bitbucket).

```
jira.example.com   wiki.example.com   bitbucket.example.com
       +                   +                    +
       |                   |                    |
       +----------------------------------------+
                           |
                           v
                         Nginx
                           +
       +-----------------------------------------+
       |                   |                     |
       v                   v                     v
   Atlassian Jira    Atlassian Confluence   Atlassian Bitbucket
 [host:jira:8080]   [host:confluence:8090]  [host:bitbucket:7990]
       +                   +                     |
       |                   |                     |
       +-----------------------------------------+
                           |
                           v
                        Postgres
                   [host:database:5432]
                           +
                           |
       +------------------------------------------+
       |                   |                      |
       v                   v                      v
   [db:jira]           [db:wiki]          [db:bitbucket]
```

In this case we will use nginx to create a Forward Proxy that will allow to redirect both Jira and Confluence from the same IP and Port using Http.

*docker-compose.yml*

	version: '3'
	services:
	  jira:
		image: cptactionhank/atlassian-jira:latest
		restart: always
		links:
		  - database
		volumes:
		  - jira-data:/var/atlassian/jira

	  confluence:
		image: cptactionhank/atlassian-confluence:latest
		restart: always
		links:
		  - database
		volumes:
		  - confluence-data:/var/atlassian/confluence

	  database:
		image: postgres:latest
		restart: always
		volumes:
		  - database-data:/var/lib/postgresql/data
	  nginx:
		image: nginx
		restart: always
		ports:
		  - "80:80"
		links:
		  - jira
		  - confluence
		volumes:
		- /vagrant:/vagrant
		command: /vagrant/deployments/nginx/scripts/run_nginx.sh
		environment:
		- DOMAIN
	volumes:
	  jira-data:
	  confluence-data:
	  database-data:

In order to execute the current docker container. It's neccessary to create new directory and copy the previous *docker-compose.yml* file to deploy.
 
	$ mkdir atlassian
	$ sudo cp /vagrant/deployments/jira-confluence/docker-compose.yml atlassian/
	
Add following domains into the host file. This is to map the host with the dns names.

	$ sudo vim /etc/hosts

    10.0.0.10 jira.domain.com www.jira.domain.com
    10.0.0.10 wiki.domain.com www.wiki.domain.com
	
> Replace 10.0.0.10 with IP of host that docker-compose command run on it.
	
Also, add the same configuration in the machines that use the http server to map the urls.
 
	Windows Hosts files  *C:\Windows\System32\drivers\etc\hosts*
	 
	10.0.0.10 domain.com
	10.0.0.10 wiki.domain.com
	10.0.0.10 jira.domain.com

Execute the current docker-compose file. This will pull and download all the different docker images and configure the using yml definition file.
	
	$ sudo docker-compose up
		
In this file there is an script (*command:*) that configures nginx with the current configuration and using a default domain *domain.com*. Also it will start nginx as a daemon with the configuration copied.

*/vagrant/deployments/nginx/scripts/run_nginx.sh*

	#!/usr/bin/env bash

	echo "################################## Run nginx"
	export DOLLAR='$'
	export DOMAIN='domain.com'
	envsubst < /vagrant/deployments/nginx/configs/nginx/nginx.conf.template > /etc/nginx/nginx.conf # /etc/nginx/conf.d/default.conf
	nginx -g "daemon off;"

> Since docker compose export an *environment:* variable from current instance, also it can be created DOMAIN env variable peviously	$ export DOMAIN=domain.com

Configure the database, by using docker and executing Posgress command to create the databases:

	$ sudo docker exec -it atlassian_database_1  psql -U postgres

Or using directly docker compose and the service name:
	
	$ sudo docker-compose exec database  psql -U postgres
	
> By default, docker-compose looks for docker-compose.yml if *-f custom-docker-compose.yml* isn-t specified.
	
1. Create Data Bases
	
   postgres=# CREATE DATABASE jira;
   postgres=# CREATE DATABASE wiki;
   
2. List Data bases in postgress

   postgres=# \l
   
3. Quit postgress command

   postgres-# \q

Browse Atlassian products:
 
 http://jira.domain.com
 http://wiki.domain.com

To see all availabe valumes create for persistence check docker valumes by using the command.
 
	$ sudo docker volume ls
 
 
### 2.3 Starting with jira and Confluence

After creating jira and confluence applications it's needed to setup the environment to start creating projects.

### 2.3.1 Starting Jira

First go to the application deployed, by default it's http://jira.domain.com

In order to use PostgreSQL database is neccessay to be added it manually. During the deployment phase, the docker container with the PostgreSQL image was created using *database* service and linked to the *jira* container. The default port for the PostgreSQL database is 5432.

[host:database:5432]

Also it was create a *jira* database at the end of the previous phase suing the command  *CREATE DATABASE jira;*

[db:jira]

Now, select manual configuration from Jira Setup Page "I'll set it up myself" and next select "My Own Database". Use the following configuration for the database:

Database Type: PostgreSQL
Hostname: database
Port: 5432
Database: jira
Username: postgres
Password: 
Schema: public

> Verify current database connection by using the test buttom at the bottom "Test Connection".

Set your company name and select the level or priviledges to create new user accounts.

The next window, you will need to have an account at MyAtlassian to Specify the license key. Then login and create a new trial license key for Jira Software. You will need following information to continue: Server ID and License Key generated.

Finally, Jira will request to create the Administrator user account to create and manage Jira. It will be needed to add some information such as username, email and password in order to proceed.

### 2.3.2 JIRA Software Installation. 

By default JIRA docker image only install JIRA Core. 
In order to install JIRA Software in order to create Agile or other types of projects you need to install this application. 

Go to the main page at http://jira.domain.com. Select Configuration -> Administration -> Applications.

On the left, it could be seen JIRA Core is already installed however JIRA Software it's not isntalled. Alos there is a Warning with an *install* button to add this feature. 

Also it could be installed additionals add-on to create and add more functionalities to JIRA and interact with another sources.

> Sometimes it will be needed to reindex some data from the database in order to correctly continue with the operations.

### 2.3.3 Verify Database

Finally, it can be verified, if jira database has been created and initialized correctly using PostgreSQL:

	postgres=# \l
	postgres=# \c jira
	jira=# \dt
	jira=# SELECT * FROM app_user; 
	
### 2.3.4 Verify Persistence

In order to verify that Containers are Statefull and maintain its persistence using the volumes configured, you can stop the docker containers and start them again by using the following commands

Stop the Containers

	$ sudo docker-container down
	
Verify containers are down

	$ sudo docker-container ps
	
Start the Containers

	$ sudo docker-container up

Test Jira is configured as it was before stopping the containers.

	http://jira.domain.com
	
### 2.4 Advanced Docker Image

Following, there is a docker-compose images that deploy the following applications with specific version.

*docker-compose.yml*

Also it uses nginx for Http server to deploy all docker services. ej. http://jira.domain.com
	- jira
	- confluence
	- bitbucket

*docker-compose.yml*

	version: '3'
	services:
	  jira:
		image: cptactionhank/atlassian-jira:7.0.5
		restart: always
		links:
		  - database
		volumes:
		  - jira-data:/var/atlassian/jira

	  confluence:
		image: cptactionhank/atlassian-confluence:5.9.4
		restart: always
		links:
		  - database
		volumes:
		  - confluence-data:/var/atlassian/confluence

	  bitbucket:
		image: atlassian/bitbucket-server:4.14
		restart: always
		links:
		  - database
		volumes:
		  - bitbucket-data:/var/atlassian/application-data/bitbucket

	  database:
		image: postgres:9.4
		restart: always
		volumes:
		  - database-data:/var/lib/postgresql/data
	  nginx:
		image: nginx
		restart: always
		ports:
		  - "80:80"
		links:
		  - jira
		  - confluence
		  - bitbucket
		volumes:
		- /vagrant:/vagrant
		command: /vagrant/deployments/nginx/scripts/run_nginx.sh
		environment:
		- DOMAIN
	volumes:
	  jira-data:
	  confluence-data:
	  bitbucket-data:
	  database-data:





