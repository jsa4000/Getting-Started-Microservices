# Install Kafka for Monitoring

#1.1 Kafka Introduction

*Kafka* is a distributed, partitioned, replicated, log service developed by LinkedIn and open sourced in 2011. Basically it is a massively scalable pub/sub message queue architected as a distributed transaction log. It was created to provide “a unified platform for handling all the real-time data feeds a large company might have”.

There are a few key differences between Kafka and other queueing systems like *RabbitMQ*, *ActiveMQ*, or *Redis’s Pub/Sub*:

- As mentioned above, it is fundamentally a replicated log service.
- It does not use AMQP or any other pre-existing protocol for communication. Instead, it uses a custom binary TCP-based protocol.
- It is very fast, even in a small cluster.
- It has strong ordering semantics and durability guarantees.

Kafka can be executed running docker containers directly or it can be installed manually. In this document we will discuss the manual method to install kafka server (kafka broker) from scratch using binaries.

Also we will configure Kafka so it can be used with *JMX* to get Metrics that Kafka provides and implements by default. Metrics in Kafka uses Yammer Metrics, and can be monitorized using JConsole and Visual VM. All the Metrics arein the Official Kafka documentation: https://github.com/amient/kafka-metrics

#1.2 Kafka Installation

- Install Java JRE and JDK.

	$ sudo apt-get update
	$ sudo apt-get -y install default-jdk
	
> Java JDK is needed to use JConsole and monitoring tools.
  
- Install Zookeeper. ZooKeeper starts as a daemon automatically on port 2181

	$ sudo apt-get -y install zookeeperd
  
>  Verify Zookeeper is running on expected port. 
 
	$ telnet localhost 2181
	
> To exit type "ruok" 
      
- Download (latest-stable) kafka binaries 

	$ wget http://ftp.jaist.ac.jp/pub/apache/kafka/0.10.0.0/kafka_2.11-0.10.0.0.tgz

 > Be sure to use the desired kafka version from http://kafka.apache.org/downloads.html
  
- Uncompress the downloaded files into a folder

	$ tar xvf kafka_2.11-0.10.0.0.tgz
	$ sudo mv kafka_2.11-0.10.0.0 /usr/local/kafka
  
- Configure Kafka Server (optionally)
  
A Kafka broker can be configured or created by editing *server.properites*. Following is described how to modify this file by allowing topics to be deleted.

	$ sudo vi /usr/local/kafka/config/server.properites

Add the following line to the end of the file
	
	delete.topic.enable = true
 
#1.3 Kafka isntallation	from Source

In order to re-compile kafka binaries from the original source code it's neccesary to install *Scala Build Tool (sbt)*. 

Following are the steps to manually install Scala Build Tool (sbt)

- First, get Scala Build Tool ubuntu repository info
  
	$ wget https://dl.bintray.com/sbt/debian/sbt-0.13.11.deb

- Install sbt repostory info

  $ sudo dpkg -i sbt-0.13.11.deb
  
- Update repository info and install 'sbt

  $ sudo apt-get update
  $ sudo apt-get install sbt
     
 
#1.4 Kafka Server Start

- Start a kafka broker by using the default configuration *server.properties*

	$ sudo /usr/local/kafka/bin/kafka-server-start.sh /usr/local/kafka/config/server.properties
 
- Open another session to start creating *topics*.

  $ sudo /usr/local/kafka/bin/kafka-topics.sh --create --topic topic-test --zookeeper localhost:2181 --partitions 1 --replication-factor 1
  Created topic "topic-test".
  
- Verify the available topics

  $ sudo /usr/local/kafka/bin/kafka-topics.sh --list --zookeeper localhost:2181
  
> You should see the created, 'topic-test' topic listed.
    
- Send message to topic as a *Producer* via the *kafka-console-producer.sh*
  
    $ echo "hello world" | sudo /usr/local/kafka/bin/kafka-console-producer.sh --broker-list localhost:9092 --topic topic-test
	
Or open a kafka console producer to start creating messages from the producer (text-type based)

    $ sudo /usr/local/kafka/bin/kafka-console-producer.sh --broker-list localhost:9092 --topic topic-test
	
- Create a *Consumer* to receive messages via *kafka-console-consumer.sh *
   
	$ sudo /usr/local/kafka/bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic topic-test --from-beginning

- Old way using Zookeper

	$ sudo /usr/local/kafka/bin/kafka-console-consumer.sh --zookeeper localhost:2181 --topic topic-test --from-beginning
 
> The '--from-beginning' flag given to start a consumer with the earliest message present in the log, rather than the latest message. (see */usr/local/kafka/bin/kafka-console-consumer.sh* help for more option details)
    
#1.5 Kafka Server Stop 

In order to Stop Kafka server it is needed to execture the following command

	$ sudo /usr/local/kafka/bin/kafka-server-stop

Sometimes Kafka server can be stopped suddenly or unmmanaged by the user. 
In these cases, some manual actions should be needed and take them into consideration:
	
- Locks when logs files are modified. This happens because some files still be opened by an application.

	sudo rm -rf /tmp/kafka-logs

- Sockets also could still be opened by kafka processes because it wasn't closed correctly. To know the processes that are using a certain Port number, the following command should be used:
	
	$ sudo lsof -i :9092
	$ sudo kill -9 <PID>
	
> Port number 9092 is the default port number that was used by kafka server and still opened.

#1.6 Kafka Configuration

- Add kafka default user 

	$ sudo useradd kafka -m
	
> It's not neccesary at all to create a specific user to use for kafka.
	
- Check hostname to be sure it's the correct to identity later.

	$ hostname

To change the hostname there are two options:
	
	$ sudo hostname <new_name>
	
Or editing the */etc/hostname* file by replacing it with the new name:
	
	$ sudo vi /etc/hostname

- Basic Considerations using vi tool:	
	
	To set vi to INSERT mode Press "Alt + O" keys
	To set back to the NORMAl mode press "ESC"

	To save the file and Quit, switch to NORMAL mode and press ":wq".
	To just Quit from vi, press ":q"

- Check current host (IP) in */etc/hosts* file configuration:
	
	$ hostname -i
	
To change the host IP from */etc/hosts* file, edit the file suing the following command:

	$ sudo vi /etc/hosts
	
> To use JMX it's needed to change the IP from hosts "127.0.0.1" with "broker.ip" localhost localhost.localdomain

- To run Kafka using JMX to start receiving metrics, its needed to set the variable JXM_PORT before start the server.

	$ sudo JXM_PORT=9999 /usr/local/kafka/bin/kafka-server-start.sh /usr/local/kafka/config/server.properties

> Also it could be set the port directly into *kafka-server-start.sh*, by adding the line:

	export JXM_PORT=9999

- Previous steps could be avoided just by manually adding to *kafka-server-start.sh* the following lines. 

	export KAFKA_HEAP_OPTS="-Xmx1G -Xms1G"
	export JMX_PORT="9999"
	export "KAFKA_JMX_OPTS=-Dcom.sun.management.jmxremote=true 
							-Dcom.sun.management.jmxremote.authenticate=false 
							-Dcom.sun.management.jmxremote.ssl=falsei 
							-Dcom.sun.management.jmxremote.rmi.port=$JMX_PORT 
							-Djava.rmi.server.hostname=*xxx.xxx.xxx.xxx(ip)*
							-Djava.net.preferIPv4Stack=true"

> Don't forget modify Djava.rmi.server.hostname, where "xxx.xxx.xxx.xxx(ip)" is the current "borker.ip" address
	
#1.7 Kafka Server with JMX

Starting brokers in Kafka is pretty straightforward, here are some simple quick start instructions. 

As developers, we want to do at least a little more than just the basics. For instance my first needs were to start multiple brokers on the same machine, and also to *Enable JMX*.

Out of the box, you can simply rely on the supplied *server.properties*. Where each broker needs a *Unique Id* and needs a *Unique Port*. These are the corresponding properties from server.properties:

	broker.id=<brokerId>
	listeners=PLAINTEXT://:<brokerPort>

> Port number 9092 is the default, if *listeners* parameter is not included in the properties file.
> In *older versions*, you used to set the port via the *port= property*.

- To start the broker, run this command

	$ sudo /usr/local/kafka/bin/kafka-server-start.sh /usr/local/kafka/config/server.properties

> Kafka comes with a ton of JMX MBeans, but you need to configure the broker to have access to those beans from a JMX tool such as JConsole or even Kafka’s own remote JMX inspection tool. Since JMX is started as part of the Java environment, the JMX config needs to be part of the jvm command line.

- The broker is started from *kafka-server-start.sh* which eventually calls *kafka-run-class.sh*, which contains the following:

	# JMX settings
	if [ -z "$KAFKA_JMX_OPTS" ]; then
	  KAFKA_JMX_OPTS="-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.authenticate=false  -Dcom.sun.management.jmxremote.ssl=false "
	fi

	# JMX port to use
	if [  $JMX_PORT ]; then
	  KAFKA_JMX_OPTS="$KAFKA_JMX_OPTS -Dcom.sun.management.jmxremote.port=$JMX_PORT "
	fi

> You could modify your version of *kafka-run-class.sh* or you can put the values into the environment. 

- To start Kafka server with *JMX enabled* it can be used the following command.

	$ sudo JMX_PORT=9990 /usr/local/kafka/bin/kafka-server-start.sh /usr/local/kafka/config/server.properties

#1.8 Multiple Kafka Server with JMX

To run multiple brokers on the same machine, the *broker.id* and *port-number* must be different for each instances. 


It could be done by simply using different *server.properties* for each instance of kafka to create. And change the settings for each file independently:

	broker.id=<broker$1>
	listeners=PLAINTEXT://:<brokerPort$1>
	

However, There is another option by using a simple script *kafka-servers-start.sh* and by setting *--override* option for each parameter.

	#!/bin/bash

	if [ $# -lt 1 ];
	then
	  echo "USAGE: kstart.sh id where id is 0..9"
	  exit 1
	fi

	sudo JMX_PORT=999$1 /usr/local/kafka/bin/kafka-server-start.sh /usr/local/kafka/config/server.properties --override broker.id=$1 --override listeners=PLAINTEXT://:909$1  --override port=909$1&
		
> To run the previous script just use the following command "kafka-servers-start.sh 1" or "kafka-servers-start.sh 2"


The script takes advantage of *--override* which overrides the *server.properties* setting. 

Following are some descritpion about the parameters that are needed to run a Kafka server instance

- Run *kafka-server-start.sh* with JMX_PORT in the enviroment, using the $1 parameter for the last digit in the port number

	sudo JMX_PORT=999$1 /usr/local/kafka/bin/kafka-server-start.sh

- The *kafka-server-start.sh* needs you to tell it exactly where the properties file lives.

	/usr/local/kafka/config/server.properties

- Override the *server.properties* broker.id using the $1 parameter

	--override broker.id=$1

- Overrides the *server.properties* listeners using the $1 parameter

	--override listeners=PLAINTEXT://:909$1

- Overrides the Port. 

	--override port=909$1
  
#1.9 Kafka Monitoring

The first requirement it's to be sure there is an instance of *Kafka server* already running using *JMX Enabled*.

To be sure that JMX is enabled:

- JMX_PORT variable must be set prior Kafka broker starts. e.j

	$ sudo JMX_PORT=9999 /usr/local/kafka/bin/kafka-server-start.sh /usr/local/kafka/config/server.properties
	
- Be sure the host IP Address is the correct one (!= 127.0.0.1) and match with the container or host IP Network Address.

	$ sudo cat /etc/hosts
	
- Verify JMX is running by using *telnet* and the JMX_Port configured previously

	> telnet subdomain.example.com 9999
	
- Verify JMX is running by using JConsole (*%jdk_path%/bin/jconsole.exe* ) and using *service:jmx:rmi:///jndi/rmi://subdomain.example.com:9999/jmx‌​rmi*

- Verify JMX is running by using Visual VM from *%jdk_path%/bin/jvisualvm.exe* or download from its official website.

	"Remote" -> "Add Remote Host" -> "New JMX connection" ->  Use "subdomain.example.com:9999"


> It's needed to install *VisualVM-MBeans* plugin  to get all the Metrics in Kafka
> It's required to have JDK installed to use *JConsole* and *Visual VM*
	

For further information about metrics go to the official documentaion from Kafka Website.

> Reference to Metrix and JMX Exporter
http://www.whiteboardcoder.com/2017/04/prometheus-and-jmx.html




