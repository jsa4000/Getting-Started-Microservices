# Prometheus Monitoring Using Docker Containers

## 0. Create Vagrant Box 

For all points explained in this document, the same Virtual Machine will be used.

The box created it's an ubuntu/xenial64 distribution. Also it will have the following libraries and tools installed:

- java jdk (latest verstion)
- docker ce (comunity edition)
- docker compose

The image is already create into /Ubuntu-Server-Docker

To run the image basically, go to the root folder and us the following command:

	Ubuntu-Server-Docker\ > vagrant up
	
It's also recommended to create an snapshot for the initial setup and another snapshot with all docker images already downloaded. In this case it will cache the data when run the containers.

	Ubuntu-Server-Docker\ > vagrant halt
	Ubuntu-Server-Docker\ > vagrant snapshot push

To start from the latest snapshot use basically:

	Ubuntu-Server-Docker\ > vagrant snapshot pop
	

## 1. Deploy Basic Prometheus Ecosystem

The idea is to create an environment that deploy a Basic Scenario for Monitoring using Docker containers.

Following are the docker container that are used in this example:

- prometheus:     Engine that tracks metrics using time-series data and store them into a database.
		          Also has implemented a query languages to create alerts and custom searchs:
- node-exporter:  exporter for monitoring performnaces for the current host machine such as: cpu usage, memory, etc..
- cadvisor -      exporter for Prometheus Monitoring Docker Containers running in Host machine
- grafana:     

### 1.1 Create docker compose file

Docker compose basically helps to run the containers by using a descriptive way instead using independent calls to docke engine.

In order to deply the containers used in this section the docker-compose.yml file is described as follows:

*docker-compose.yml*

	version: '3'

	services:
	  prometheus:
		image: prom/prometheus:latest
		container_name: prometheus
		volumes:
		  - /vagrant:/vagrant
		  - prometheus_data:/prometheus
		ports:
		  - '9090:9090'
		command:
		  - '-config.file=/vagrant/deployments/basic-monitoring/prometheus.yml'

	  node-exporter:
		image: prom/node-exporter:latest
		container_name: node-exporter
		ports:
		 - '9100:9100'

	  cadvisor:
		image: google/cadvisor:latest
		container_name: cadvisor
		volumes:   
		  # It's needed to declare these volumes, so cadvisor knows the containers deployed and running in Host machine.
		  - "/:/rootfs:ro"
		  - "/sys:/sys:ro"
		  - "/var/lib/docker/:/var/lib/docker:ro"
		  - "/var/run/docker.sock:/var/run/docker.sock"
		ports:
		 - '8080:8080'

	  grafana:
		image: grafana/grafana
		container_name: grafana
		volumes:
		 - grafana_data:/var/lib/grafana
		depends_on:
		  - prometheus
		ports:
		  - "3000:3000"

	volumes:
	  prometheus_data: 
	  grafana_data: 

In the previous file specification some considerations need to be explained:

- container_name: is the name used for docker-compose to create the images internally
- ports: This is used to map the port between the docker container and the Host machine. "HOST_PORT:DOCKER_PORT" 
- depends_on: this specifies a lsit of services that are needed prior to create run the current one. 
- command: this is used to launch a command from docker-compose. There are different ways to specify the command to run:

	Single Command
	
		command: start.sh -args "parameter_sample"
		
	Multiple Commands:
	
		command:
			- "start.sh"
			- "-args"
			- "parameter_sample"
	Multiple Commands with ENTRYPOINT (defined in dockerfile):
		
		ENTRYPOINT ["start.sh"]
	
		command:
			- "-args"
			- "parameter_sample"
			
	Multiple Commands (similar to dockerfile):
	
		command: ["start.sh", "-args", "parameter_sample"]
		

- volumes: this maps the folders and files between the host and the docker container. "HOST_VOLUME:DOCKER_VOLUME".

  It's used to convert docker images into persistant instances (stateful). So it will be used in different executions.
  
		- /vagrant/bin:/etc/bin -> folders mapping
		- /vagrant/bin/start-app.sh:/etc/bin/start-app.sh -> files mapping
	
  Note: If HOST_VOLUME is not specified, docker will create a new folder where DOCKER_VOLUME store the data.
	
  Last *volumes* tag (same level as services) is used to reuse volumes. Also is the name as the folder created internally by docker.(--from-volume in docker)
	
	services:
	
	  service1: 
	    image: ...
		volumes:
		 - shared-folder:/etc/local  #Reuse the volume shared-folder
		 
	  service2:
	    build: .
		image: name_build
		volumes:
		 - shared-folder:/etc/usr/config  #Reuse the volume shared-folder
		 
	volumes:
		- shared-folder:
			driver: local  #This is optional and it tell the type of driver used
			
  To list all the volumes created by docker use:
	
	$ docker volumes ls
	
- links: this is used to list all the containers that are linked to this container. ej. http://service1:8080, http://prometheus:9090

 Note: All containers within the same docker-compose file are shared by default.
	
In order to deply the docker-compose, it can be used the following methods:

- Using the *docker-compose.yml* in the current folder:

	$ sudo docker-compose up
	
- Using a custom docker-compose file:

	$ sudo docker-compose -f "custom-docker-compose.yml" up

- Running specific service inside the docker-compose file:

	$ sudo docker-compose run service1
	
Note: Every operation for the particular docker-compose must be specified: up, down, run, ps, etc..
	
To Stop a docker container

	$ sudo docker-compose down
	
	
### 1.2 Start Monitoring

Prior to start monitoring and get all metrics from the current environment, it's needed to configure prometheus properly.

This is done statically by defining all the services (scrappers) in prometheus configuration file

	*prometheus.yml*

	global:
	  scrape_interval:     5s 
	  evaluation_interval: 5s 
	  external_labels:
		monitor: 'my-monitor'

	scrape_configs:
	  - job_name: 'prometheus'
		static_configs:
		 - targets: ['localhost:9090']

	  - job_name: 'node-exporter'
		static_configs:
		 - targets: ['node-exporter:9100']

	  - job_name: 'cadvisor'
		static_configs:
		 - targets: ['cadvisor:8080']
		 
This file is passed through docker-compose while prometheus service definition:

 prometheus:
		image: prom/prometheus:latest
		container_name: prometheus
		volumes:
		  - /vagrant:/vagrant
		  - prometheus_data:/prometheus
		ports:
		  - '9090:9090'
		command:
		  - '-config.file=/vagrant/deployments/basic-monitoring/prometheus.yml'
		  
Note: Also the data are stored persistenly by using a shared volume *prometheus_data*

In order to start monitoring the current environment, it's needed to start the docker container previously.

	$ sudo docker-compose up
	
Verify, it can be accessed through all the services to check the metrics and if they are running properly.

	- Prometheus Metrics: http://10.0.0.10:9090/metrics
	- Node Exporter Metrics:  http://10.0.0.10:9100/metrics
	- Cadvisor metrics: http://10.0.0.10:8000/metrics

Verify the targets configured in *prometheus* are running at ttp://10.0.0.10:9090

### 1.3 Dashboard for Monitoring

There are two ways to monitoring prometheus

- Prometheus Dashboard

To access to Prometheus Dashboard use the following url http://10.0.0.10:9090

This dashboard is primarly used for Testing pourpose.
. 

- Grafana Dashboard

 http://10.0.0.10:3000
 
 
Node Exporter Single Server
https://grafana.com/dashboards/22
cadvisro grafana dashboard (Docker-monitoring)
https://grafana.com/dashboards/193
Node Exporter Server Metrics
https://grafana.com/dashboards/405




## 2. Alerting With Prometheus

## 2.2. Benchmarking for Linux

To test the Alerts it's needed to simulate that contraint to be triggered accordingly.

	ALERT high_load
	  IF node_load1 > 0.5
	  
For this altert it will be simulated a CPU overloads by performing a stress benchmark into the current node.

1. Installing sysbench

On Debian/Ubuntu, sysbench can be installed as follows:

	$ sudo apt-get -y install sysbench

> Take a look at the commands using:  man sysbench

2. Run the Benshmark using CPU

	$ sudo sysbench --test=cpu --cpu-max-prime=50000 --num-threads=4 run 

> It can be used another additional parameters such as the maximun amount of time to execute this test: --max-time=6000
	
3. Run the Benshmark using IO
	
	$ sudo sysbench --test=fileio --file-total-size=10G prepare
	$ sudo sysbench --test=fileio --file-total-size=10G --file-test-mode=rndrw --init-rng=on --max-time=300 --max-requests=0 run

4. Use *Top* command to look and monitoring real-time performances

	$ sudo top
	
> It can be used additional tools (more fancy) for monitoring processes such as htop, atop, etc.. However it's needed to isntall these applications previously.

5. Verify if the alters has been triggered in AlertManager on Prometheus.

	http://10.0.0.10:9090/alerts

## 3. Discovery Services with Prometheus 


## 4. Kafka Cluster Monitoring




curl localhost:8500/v1/catalog/services

sudo docker-compose -f docker-compose-kafka-multibroker.yml down

http://10.0.0.10:8500/ui/#/dc1/services

sudo docker-compose run kafka bash


sudo docker run --rm wurstmeister/kafka kafka-topics.sh --create --topic test --replication-factor 1 --partitions 1 --zookeeper localhost:2181

docker run --rm --network kafka-net ches/kafka \
>   kafka-topics.sh --list



  scrape_interval: 5s
  scrape_timeout: 5s