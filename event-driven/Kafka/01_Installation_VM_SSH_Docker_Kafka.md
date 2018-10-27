# Installation

##1. Install Virtual Machine (Linux)

> In computing, a **virtual machine (VM)** is an emulation of a computer system. Virtual machines are based on computer architectures and provide functionality of a physical computer. Their implementations may involve specialized hardware, software, or a combination.

To install a Virtual Machine on a PC, a set of tools can be used to allow virtualization. For this process Virtual Box will be used.

The Steps for the installation process can be seen using the following link:

https://pods.iplantcollaborative.org/wiki/display/HDFDE/Installing+VirtualBox%2C+Ubuntu%2C+and+Docker

The OS installed will be Ubunto 17.04 Desktop amd64. The process is straight-foward in the sense that the tools often guide the installation process through a Wizard. Some recomendations to take into Account:
- Set the proper amount of RAM, depending on your System capacity.
- Configure Networks Adapters to use between the Vitual Machine and the Host OS. 
	- NAT. Used to share connection between host and the Virtual Machine. (This doesn't create a virtual Network between the two)
	- Bridged Adapter. Create a LAN Network (bridge) between the Host and the Virtual Michine. (Recommended). (https://www.youtube.com/watch?v=5BsShkcweIs)
- Set the Storage with a dynamic range so it can be updrage in the future when needed.
- Set some features between OS such as: Shared clipboard, drag&drop etc.

After the installation, the tool allow to create a snapshot for the Vistual Machine. In this case it is useful to have a copy of the OS clean.

> Be sure your Computer allows Virtualization. On the other hand this will be emulated so the performances will be worst.

##2. Configure SSH Protocol

- Install OpenSSH onto Ubuntu machine.

> **Secure Shell (SSH)** is a cryptographic network protocol for operating network services securely over an unsecured network. The best known example application is for remote login to computer systems by users.

Some Linux distributions don't have SSH installed by default (Ubuntu). We can see it by using the following command to connect to a SSH Host.

	> ssh root@192.168.1.142
	> ssh: connect to host 192.168.1.142 port 22: Connection refused

To install and run SSH service:	

	~$ sudo apt-get install openssh-server 
	~$ sudo service ssh status
	~$ vi /etc/ssh/sshd_config

	# Configure and set user permissions to ssh config file
	~$ sudo cp /etc/ssh/sshd_config /etc/ssh/sshd_config.factory-defaults
	~$ sudo chmod a-w /etc/ssh/sshd_config.factory-defaults
	~$ sudo restart ssh or sudo systemctl restart ssh

Some references and links to install and configure SSH (allow users, private keys, etc..):

> https://www.youtube.com/watch?v=0KXZ6GnVza8
> https://help.ubuntu.com/community/SSH/OpenSSH/Configuring
> https://support.rackspace.com/how-to/connecting-to-a-server-using-ssh-on-linux-or-mac-os/

- There are some ways to connect through SSH such as: Putty, Windows 10 Bash, Linux Terminal, etc. The generic to connect to an SSH host is the following

ssh root@192.168.1.142 or ssh 192.168.1.142 


> https://mediatemple.net/community/products/dv/204403684/connecting-via-ssh-to-your-server

##3. Install Docker ( Community Edition)

> **Docker** is a software technology providing containers, promoted by the company Docker, Inc. Docker provides an additional layer of abstraction and automation of operating-system-level virtualization on Windows and Linux. Docker uses the resource isolation features of the Linux kernel such as cgroups and kernel namespaces, and a union-capable file system such as OverlayFS and others to allow independent "containers" to run within a single Linux instance, avoiding the overhead of starting and maintaining *virtual machines (VMs)*.

You can install Docker CE in different ways, depending on your needs:

-Most users set up Docker’s repositories and install from them, for ease of installation and upgrade tasks. This is the recommended approach.
- Some users download the DEB package and install it manually and manage upgrades completely manually. This is useful in situations such as installing Docker on air-gapped systems with no access to the internet.
- In testing and development environments, some users choose to use automated convenience scripts to install Docker.

Install using the repository (First Setup the repository)

    1. Update the apt package index:

    $ sudo apt-get update

    2. Install packages to allow apt to use a repository over HTTPS:

    $ sudo apt-get install \
        apt-transport-https \
        ca-certificates \
        curl \
        software-properties-common

    3. Add Docker’s official GPG key:

    $ curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -

	Verify that the key fingerprint is 9DC8 5822 9FC7 DD38 854A E2D8 8D81 803C 0EBF CD88.

		$ sudo apt-key fingerprint 0EBFCD88

		pub   4096R/0EBFCD88 2017-02-22
			  Key fingerprint = 9DC8 5822 9FC7 DD38 854A  E2D8 8D81 803C 0EBF CD88
		uid                  Docker Release (CE deb) <docker@docker.com>
		sub   4096R/F273FCD8 2017-02-22

    4. Use the following command to set up the stable repository. You always need the stable repository, even if you want to install builds from the edge or test repositories as well. To add the edge or test repository, add the word edge or test (or both) after the word stable in the commands below.

        Note: The lsb_release -cs sub-command below returns the name of your Ubuntu distribution, such as xenial. Sometimes, in a distribution like Linux Mint, you might have to change $(lsb_release -cs) to your parent Ubuntu distribution. For example, if you are using Linux Mint Rafaela, you could use trusty.

    $ sudo add-apt-repository \
       "deb [arch=amd64] https://download.docker.com/linux/ubuntu \
       $(lsb_release -cs) \
       stable"

  
	5. Install the latest version of Docker CE, or go to the next step to install a specific version. Any existing installation of Docker is replaced.

    $ sudo apt-get update		-> Force to update the properties and 
	$ sudo apt-get install docker-ce

	
	6. Verify that Docker CE is installed correctly by running the hello-world image.

	$ sudo docker run hello-world


> Basically
>	$ sudo apt-get install apt-transport-https ca-certificates curl software-properties-common
>	$ curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
>	$ sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu xenial stable"
>	$ sudo apt-get update	
>	$ apt-cache search docker-ce
>	$ sudo docker run hello-world

##4. Install Kafka

> Apache Kafka is an open-source scalable and high-throughput messaging system developed by the Apache Software Foundation written in Scala. Apache Kafka is specially designed to allow a single cluster to serve as the central data backbone for a large environment. It has a much higher throughput compared to other message brokers systems like ActiveMQ and RabbitMQ. It is capable of handling large volumes of real-time data efficiently. You can deploy Kafka on single Apache server or in a distributed clustered environment.

1. Update/upgrade Linex OS Server

	$ sudo apt-get update -y
	$ sudo apt-get upgrade -y
	
2. Installa Oracle JDK 8

sudo add-apt-repository -y ppa:webupd8team/java
sudo apt-get update
sudo apt-get install oracle-java8-installer -y
sudo java -version

3. Install Kafka (Single-Broker configuration)

3.1 Donwload the latest version of Kafka from apache website

wget http://apache.crihan.fr/dist/kafka/0.11.0.0/kafka_2.11-0.11.0.0.tgz

3.2 Create a directory for Kafka installation

sudo mkdir /opt/Kafka
cd /opt/Kafka

3.3 Extract the downloaded archive using tar command in /opt/Kafka:

sudo tar -xvf kafka_2.10-0.10.0.1.tgz -C /opt/Kafka/

3.4 Start Zookeeper (default configuration)

bin/zookeeper-server-start.sh config/zookeeper.properties

3.4 Start Kafka (default configuration)

bin/kafka-server-start.sh config/server.properties

3.5 Create a Topic

bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic test
bin/kafka-topics.sh --list --zookeeper localhost:2181
 
3.6 Create a Producer 
 
bin/kafka-console-producer.sh --broker-list localhost:9092 --topic test
 
3.7 Start a Consumer 

bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic test --from-beginning


> https://kafka.apache.org/quickstart
> https://devops.profitbricks.com/tutorials/install-and-configure-apache-kafka-on-ubuntu-1604-1/
> https://medium.com/@harittweets/getting-started-with-apache-kafka-fe5a0b10a3be

##4. Install Kafka Manager

4.1 Get kafka-Manager for its repository in Github
wget https://github.com/yahoo/kafka-manager/archive/master.zip
unzip master.zip
mv kafka-manager-master/ kafka-manager
cd kafka-manager

4.2 Install Scala
$ sudo apt-get remove scala-library scala
$ sudo wget https://downloads.lightbend.com/scala/2.12.3/scala-2.12.3.deb
$ sudo dpkg -i scala-2.12.3.deb
$ scala -version

$ echo "deb https://dl.bintray.com/sbt/debian /" | sudo tee -a /etc/apt/sources.list.d/sbt.list
$ sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 2EE0EA64E40A89B84B2DF73499E82A75642AC823
$ sudo apt-get update
$ sudo apt-get install sbt

4.3 Build Poject Solution

sbt clean dist

4.4 Unzip the compiled package

sudo mv target/universal/kafka-manager-1.1.zip ~/
cd ~/
unzip kafka-manager-1.1.zip
rm kafka-manager-1.1.zip

4.4 Run Kafka-Manager

sudo bin/kafka-manager -Dkafka-manager.zkhosts=”localhost:2181″

http://edbaker.weebly.com/blog/install-and-evaluation-of-yahoos-kafka-manager
http://chennaihug.org/knowledgebase/yahoo-kafka-manager/











