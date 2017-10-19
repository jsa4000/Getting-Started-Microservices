# Installating Kafka Ecosystem

###1.1 Installation Pre-requisites

#### Installing Java Runtime JDK 8.0 (or Preferred one)

Webupd8team has made a very good job in preparing installation packages for the official oracle java. You can follow these three steps and install it correctly and add the webupd8team update server to your system so that a new java is installed whenever it's released automatically.

- Add the server by typing: 
	~$ sudo add-apt-repository ppa:webupd8team/java

- Update your system: 
	~$ sudo apt-get update.

- Install both oracle-java8-installer and oracle-java8-set-default by typing: 
	~$ sudo apt-get install oracle-java8-installer oracle-java8-set-default

#### Installing Docker (Docker-compose)
	  
- Update the apt package index:

	~$ sudo apt-get update

- Install packages to allow apt to use a repository over HTTPS:

	~$ sudo apt-get install \
		apt-transport-https \
		ca-certificates \
		curl \
		software-properties-common

- Add Docker’s official GPG key:

	~$ curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
	~$ sudo apt-get update

- Use the following command to set up the stable repository.
	
	~$ sudo add-apt-repository \
	   "deb [arch=amd64] https://download.docker.com/linux/ubuntu \
	   $(lsb_release -cs) \
	   stable"
	~$sudo apt-get update
	
- Install the latest version of Docker CE
	
	~$ sudo apt-get install docker-ce
	
- Verify Installation
	
	~$ sudo docker version
	~$ sudo docker run hello-world
	~$ sudo docker image list
  
##1.2 Confluent 3.3.0 (Kafka Package)

#### Installation Confluent

- Consideration before the installation (also production environment)

http://docs.confluent.io/current/kafka/deployment.html
http://docs.confluent.io/current/kafka/post-deployment.html
http://docs.confluent.io/current/installation.html#installation-apt
https://simplydistributed.wordpress.com/2016/11/29/scaling-out-with-kafka-consumer-groups/

- Install confluent by following the next Steps

	~$ sudo add-apt-repository "deb [arch=amd64] http://packages.confluent.io/deb/3.3 stable main"
	~$ sudo apt-get update && sudo apt-get install confluent-platform-oss-2.11

- Start Zookeper, Kafka and Scheme registry

	~$ confluent start [service_name]
	
	where service_name could be zookeper, kafka, etc.. It will autonatically detect neccesary services to start the service passed through parameters.
	
- Confuent executable perform a centralizad way to control the entire Kafka ecosystem: start, stop, destroy, install a connector, get status of services being run, etc..

####1.2.1 - Confluent with Docker

- Start Zookeeper

	~$ sudo docker run -d \
		--net=host \
		--name=zookeeper \
		-e ZOOKEEPER_CLIENT_PORT=32181 \
		confluentinc/cp-zookeeper:3.3.0
		
  Verify the Service us running correctly		
	
	~$ sudo docker logs zookeeper

- Start Kafka		
		
	~$ sudo docker run -d \
		--net=host \
		--name=kafka \
		-e KAFKA_ZOOKEEPER_CONNECT=localhost:32181 \
		-e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:29092 \
		-e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 \
		confluentinc/cp-kafka:3.3.0
		
- Create a Topic (from another Docker)

	~$ sudo docker run \
	  --net=host \
	  --rm confluentinc/cp-kafka:3.3.0 \
	  kafka-topics --create --topic foo --partitions 1 --replication-factor 1 --if-not-exists --zookeeper localhost:32181	
			
  
  Verify the topic was succesfully installed (from docker)

	~$ sudo docker run \
	  --net=host \
	  --rm \
	  confluentinc/cp-kafka:3.3.0 \
	  kafka-topics --describe --topic foo --zookeeper localhost:32181		

- Create Producer (and a sequence of 42 messages ) 
	
	~$ sudo docker run \
	  --net=host \
	  --rm \
	  confluentinc/cp-kafka:3.3.0 \
	  bash -c "seq 42 | kafka-console-producer --request-required-acks 1 --broker-list localhost:29092 --topic foo && echo 'Produced 42 messages.'"
	  
- Create Consumer
 	  
	~$ sudo docker run \
	  --net=host \
	  --rm \
	  confluentinc/cp-kafka:3.3.0 \
	  kafka-console-consumer --bootstrap-server localhost:29092 --topic foo --new-consumer --from-beginning --max-messages 42	  

####1.2.2 - Schema Registry	(Confluent Platform)

- Start Schema-Registry

	~$ sudo docker run -d \
	  --net=host \
	  --name=schema-registry \
	  -e SCHEMA_REGISTRY_KAFKASTORE_CONNECTION_URL=localhost:32181 \
	  -e SCHEMA_REGISTRY_HOST_NAME=localhost \
	  -e SCHEMA_REGISTRY_LISTENERS=http://localhost:8081 \
	  confluentinc/cp-schema-registry:3.3.0
	  
- Create an **interactive** session to start "produccing" messages

	~$ sudo docker run -it --net=host --rm confluentinc/cp-schema-registry:3.3.0 bash  
	  
	Direct the utility at the local Kafka cluster, tell it to write to the topic bar, read each line of input as an Avro message, validate the schema against the Schema Registry at the specified URL, and finally indicate the format of the data. Notice the --property value.schema=' ' to create the schema (type) for the messages that will be sent.

	~$ /usr/bin/kafka-avro-console-producer \
	  --broker-list localhost:29092 --topic bar \
	  --property value.schema='{"type":"record","name":"myrecord","fields":[{"name":"f1","type":"string"}]}'

	Once started, the process will wait for you to enter messages, one per line, and will send them immediately when you hit the Enter key. Try entering a few messages:

	{"f1": "value1"}
	{"f1": "value2"}
	{"f1": "value3"}

	When you’re done, use Ctrl+C or Ctrl+D to stop the producer client. You can then type exit to leave the container altogether. Now that we’ve written avro data to Kafka, we should check that the data was actually produced as expected to consume it. Although the Schema Registry also **ships** with a built-in **console consumer utility**, we’ll instead demonstrate how to read it from outside the container on our local machine via the REST Proxy. The **REST Proxy **depends on the **Schema Registry** when **producing/consuming avro **, so we’ll need to pass in the details for the detached Schema Registry container we launched above
	
	
####1.2.3 - Rest Proxy	(Confluent Platform)
	  	  
- Run Rest Proxy

	~$ sudo docker run -d \
	  --net=host \
	  --name=kafka-rest \
	  -e KAFKA_REST_ZOOKEEPER_CONNECT=localhost:32181 \
	  -e KAFKA_REST_LISTENERS=http://localhost:8082 \
	  -e KAFKA_REST_SCHEMA_REGISTRY_URL=http://localhost:8081 \
	  -e KAFKA_REST_HOST_NAME=localhost \
	  confluentinc/cp-kafka-rest:3.3.0	  
	  
- Cosume the messages from Topic using Rest Proxy

	~$ sudo docker run -it --net=host --rm confluentinc/cp-schema-registry:3.3.0 bash
  
  The first step in consuming data via the REST Proxy is to create a consumer instance (inside Rest Proxy).

	~$ curl -X POST -H "Content-Type: application/vnd.kafka.v1+json" \
	  --data '{"name": "my_consumer_instance", "format": "avro", "auto.offset.reset": "smallest"}' \
	  http://localhost:8082/consumers/my_avro_consumer

  Our next curl command will retrieve data from a topic in our cluster (bar in this case). The messages will be decoded, translated to JSON, and included in the response. The schema used for deserialization is retrieved automatically from the Schema Registry service, which we told the REST Proxy how to find by setting the KAFKA_REST_SCHEMA_REGISTRY_URL variable on startup.	  
	  
  curl -X GET -H "Accept: application/vnd.kafka.avro.v1+json" \
  http://localhost:8082/consumers/my_avro_consumer/instances/my_consumer_instance/topics/bar


####1.2.4 - Stream Monitoring	Confluent Enterprise)

The Control Center application provides enterprise-grade capabilities for monitoring and managing your Confluent deployment. Control Center is part of the Confluent Enterprise offering; a trial license will support the image for the first 30 days after your deployment.

Confluent Control-Center provides alerting functionality to notify you when anomalous events occur in your cluster. This section assumes the console producer and consumer we launched to illustrate the stream monitoring features are still running in the background.

- Start Confluent Control Center

	~$ sudo docker run -d \
	  --name=control-center \
	  --net=host \
	  --ulimit nofile=16384:16384 \
	  -p 9021:9021 \
	  -v /tmp/control-center/data:/var/lib/confluent-control-center \
	  -e CONTROL_CENTER_ZOOKEEPER_CONNECT=localhost:32181 \
	  -e CONTROL_CENTER_BOOTSTRAP_SERVERS=localhost:29092 \
	  -e CONTROL_CENTER_REPLICATION_FACTOR=1 \
	  -e CONTROL_CENTER_MONITORING_INTERCEPTOR_TOPIC_PARTITIONS=1 \
	  -e CONTROL_CENTER_INTERNAL_TOPICS_PARTITIONS=1 \
	  -e CONTROL_CENTER_STREAMS_NUM_STREAM_THREADS=2 \
	  -e CONTROL_CENTER_CONNECT_CLUSTER=http://localhost:28082 \
	  confluentinc/cp-enterprise-control-center:3.3.0
	  
  This docker container will require volume data. The data will be stored in local machine. It will be mapped from (docker) /var/lib/confluent-control-center to (local) /tmp/control-center/data.
	  
  Control Center will create the topics it needs in Kafka. Check that it started correctly by searching it’s logs with the following command:

	~$ sudo docker logs control-center | grep Started

  To see the Control Center UI, open the link http://localhost:9021 in your browser.
	  
- Create the Topic to start monitoring

	~$ sudo docker run \
	  --net=host \
	  --rm confluentinc/cp-kafka:3.3.0 \
	  kafka-topics --create --topic c3-test --partitions 1 --replication-factor 1 --if-not-exists --zookeeper localhost:32181
	  

- Produce some messages 
	  	  
	~$ sudo docker run \
		--net=host \
		--rm \
		-e CLASSPATH=/usr/share/java/monitoring-interceptors/monitoring-interceptors-3.3.0.jar \
		confluentinc/cp-kafka-connect:3.3.0 \
		bash -c 'seq 10000 | kafka-console-producer --request-required-acks 1 --broker-list localhost:29092 --topic c3-test --producer-property interceptor.classes=io.confluent.monitoring.clients.interceptor.MonitoringProducerInterceptor --producer-property acks=1 && echo "Produced 10000 messages."'
		
- Consume some messages

	~$ sudo docker run \
	--net=host \
	--rm \
	-e CLASSPATH=/usr/share/java/monitoring-interceptors/monitoring-interceptors-3.3.0.jar \
	confluentinc/cp-kafka-connect:3.3.0 \
	bash -c 'kafka-console-consumer --consumer-property group.id=qs-consumer --consumer-property interceptor.classes=io.confluent.monitoring.clients.interceptor.MonitoringConsumerInterceptor --new-consumer --bootstrap-server localhost:29092 --topic c3-test --offset 0 --partition 0 --max-messages=1000'
	
####1.2.5 -  Schema Registry

http://docs.confluent.io/current/schema-registry/docs/intro.html#quickstart
https://github.com/confluentinc/examples/blob/3.1.x/kafka-streams/src/main/java/io/confluent/examples/streams/WikipediaFeedAvroLambdaExample.java



####1.2.6 - Landoop Installation (Another Control-Center manager)

- Run docker image by typing using the following Docher hub image l**andoop/fast-data-dev** and using the current **default** configuration in Confluent platform (Zookeper). This configuration can be modifed using enevironment variables passed through the docker isntancing.

	~$ sudo docker run --rm --net=host landoop/fast-data-dev

When you need:
- Confluent OSS with Apache Kafka including: ZooKeeper, Schema Registry, Kafka REST, Kafka-Connect
- Landoop Fast Data Tools including: kafka-topics-ui, schema-registry-ui, kafka-connect-ui
- 20+ Kafka Connectors to simplify ETL processes
- Integration testing and examples embedded into the docker

####1.2.7 - Intersting Topics

https://www.confluent.io/blog/exactly-once-semantics-are-possible-heres-how-apache-kafka-does-it/


###1.3 Docker Compose

>Docker Compose is a powerful tool that enables you to launch multiple docker images in a coordinated fashion. It is ideal for platforms like Confluent. Before you get started, you will need to install both the core Docker Engine and Docker Compose. Once you’ve done that, you can follow the steps below to start up the Confluent Platform services.

#### Installing Docker Composite

- To uninstall Docker Compose if you installed using curl:
	~$ sudo rm /usr/local/bin/docker-compose

- Run this command to download Docker Compose, replacing $dockerComposeVersion with the specific version of Compose you want to use:

	~$ sudo curl -L
		https://github.com/docker/compose/releases/download/$dockerComposeVersion/docker-compose-`uname
		-s`-`uname -m` -o /usr/local/bin/docker-compose

	Where $dockerComposeVersion can be found in  https://github.com/docker/compose/releases
	
	e.g.
	
	~$ sudo curl -L https://github.com/docker/compose/releases/download/1.16.0-rc1/docker-compose-`uname -s`-`uname -m` -o /usr/local/bin/docker-compose
	~$ sudo chmod +x /usr/local/bin/docker-compose

- Verify installation
	
	~$ docker-compose --version
	
#### Run Docker Composite	
	
- FirstGet docker-compose.yml with the definition of the images to load

- In the same path use the following commands

	~$ sudo docker-compose create
	~$ sudo docker-compose start

	or
	
	~$ sudo docker-compose up - d
	
#### create/start vs up

	- docker-compose start

		Starts existing containers for a service.

	- docker-compose up

		Builds, (re)creates, starts, and attaches to containers for a service.

		Linked services will be started, unless they are already running.

		By default, docker-compose up will aggregate the output of each container and, when it exits, all containers will be stopped. Running docker-compose up -d, will start the containers in the background and leave them running.

	By default, if there are existing containers for a service, docker-compose up will stop and recreate them (preserving mounted volumes with volumes-from), so that changes in docker-compose.yml are picked up. If you do not want containers stopped and recreated, use docker-compose up --no-recreate. This will still start any stopped containers, if needed.
	

#### Functions to stop/remove containers

	stop all containers:
	~$ sudo docker kill $(sudo docker ps -q)

	remove all containers
	~$ sudo docker rm $(sudo docker ps -a -q)

	remove all docker images
	~$ sudo docker rmi $(sudo docker images -q)

	
#### File Definition Docker consumer examples
	
- docker-compose.yml

---
version: '2'
services:
  zookeeper:
    image: confluentinc/cp-zookeeper:latest
    network_mode: host
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000
    extra_hosts:
      - "moby:127.0.0.1"

  kafka:
    image: confluentinc/cp-kafka:latest
    network_mode: host
    depends_on:
      - zookeeper
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: localhost:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
    extra_hosts:
      - "moby:127.0.0.1"

	
- docker-compose.yml	
 (https://github.com/confluentinc/cp-docker-images/blob/3.3.x/examples/cp-all-in-one/docker-compose.yml)
 
---
version: '2'
services:
  zookeeper:
    image: confluentinc/cp-zookeeper
    hostname: zookeeper
    ports:
      - "2181:2181"
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000

  broker:
    image: confluentinc/cp-enterprise-kafka
    hostname: broker
    depends_on:
      - zookeeper
    ports:
      - "9092:9092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: 'zookeeper:2181'
      KAFKA_ADVERTISED_LISTENERS: 'PLAINTEXT://broker:9092'
      KAFKA_METRIC_REPORTERS: io.confluent.metrics.reporter.ConfluentMetricsReporter
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
      KAFKA_GROUP_INITIAL_REBALANCE_DELAY_MS: 0
      CONFLUENT_METRICS_REPORTER_BOOTSTRAP_SERVERS: broker:9092
      CONFLUENT_METRICS_REPORTER_ZOOKEEPER_CONNECT: zookeeper:2181
      CONFLUENT_METRICS_REPORTER_TOPIC_REPLICAS: 1
      CONFLUENT_METRICS_ENABLE: 'true'
      CONFLUENT_SUPPORT_CUSTOMER_ID: 'anonymous'

  schema_registry:
    image: confluentinc/cp-schema-registry
    hostname: schema_registry
    depends_on:
      - zookeeper
      - broker
    ports:
      - "8081:8081"
    environment:
      SCHEMA_REGISTRY_HOST_NAME: schema_registry
      SCHEMA_REGISTRY_KAFKASTORE_CONNECTION_URL: 'zookeeper:2181'

  connect:
    image: confluentinc/cp-kafka-connect
    hostname: connect
    depends_on:
      - zookeeper
      - broker
      - schema_registry
    ports:
      - "8083:8083"
    environment:
      CONNECT_BOOTSTRAP_SERVERS: 'broker:9092'
      CONNECT_REST_ADVERTISED_HOST_NAME: connect
      CONNECT_REST_PORT: 8083
      CONNECT_GROUP_ID: compose-connect-group
      CONNECT_CONFIG_STORAGE_TOPIC: docker-connect-configs
      CONNECT_CONFIG_STORAGE_REPLICATION_FACTOR: 1
      CONNECT_OFFSET_FLUSH_INTERVAL_MS: 10000
      CONNECT_OFFSET_STORAGE_TOPIC: docker-connect-offsets
      CONNECT_OFFSET_STORAGE_REPLICATION_FACTOR: 1
      CONNECT_STATUS_STORAGE_TOPIC: docker-connect-status
      CONNECT_STATUS_STORAGE_REPLICATION_FACTOR: 1
      CONNECT_KEY_CONVERTER: io.confluent.connect.avro.AvroConverter
      CONNECT_KEY_CONVERTER_SCHEMA_REGISTRY_URL: 'http://schema_registry:8081'
      CONNECT_VALUE_CONVERTER: io.confluent.connect.avro.AvroConverter
      CONNECT_VALUE_CONVERTER_SCHEMA_REGISTRY_URL: 'http://schema_registry:8081'
      CONNECT_INTERNAL_KEY_CONVERTER: org.apache.kafka.connect.json.JsonConverter
      CONNECT_INTERNAL_VALUE_CONVERTER: org.apache.kafka.connect.json.JsonConverter
      CONNECT_ZOOKEEPER_CONNECT: 'zookeeper:2181'

  control-center:
    image: confluentinc/cp-enterprise-control-center
    hostname: control-center
    depends_on:
      - zookeeper
      - broker
      - schema_registry
      - connect
    ports:
      - "9021:9021"
    environment:
      CONTROL_CENTER_BOOTSTRAP_SERVERS: 'broker:9092'
      CONTROL_CENTER_ZOOKEEPER_CONNECT: 'zookeeper:2181'
      CONTROL_CENTER_CONNECT_CLUSTER: 'connect:8083'
      CONTROL_CENTER_REPLICATION_FACTOR: 1
      CONTROL_CENTER_INTERNAL_TOPICS_PARTITIONS: 1
      CONTROL_CENTER_MONITORING_INTERCEPTOR_TOPIC_PARTITIONS: 1
      CONFLUENT_METRICS_TOPIC_REPLICATION: 1
PORT: 9021
	
	
#### Why is advertised listeners needed? 

When a client connect to a Kafka cluster by specifying "bootstrap.servers=...", it do not have to be a full list of all Kafka brokers, because client doesn't use it to send data to Kafka directly. Rather, client will somehow find zookeeper, and get the whole list of Kafka brokers(may be part of them that are needed for certain topic) from zookeeper. So "ADVERTISED_LISTENER" is used for client to connect to Kafka.

Basically.

- **listeners** is what the broker will use to create server sockets.
- **advertised.listeners** is what clients will use to connect to the brokers.

The two settings can be different if you have a "complex" network setup (with things like public and private subnets and routing in between).

- From Release 0.9.0.0 notes:

    During the 0.9.0.0 release cycle, support for multiple listeners per broker was introduced. Each listener is associated with a security protocol, ip/host and port. When combined with the advertised listeners mechanism, there is a fair amount of flexibility with one limitation: at most one listener per security protocol in each of the two configs (listeners and advertised.listeners).

    In some environments, one may want to differentiate between external clients, internal clients and replication traffic independently of the security protocol for cost, performance and security reasons. A few examples that illustrate this:

        Replication traffic is assigned to a separate network interface so that it does not interfere with client traffic.
        External traffic goes through a proxy/load-balancer (security, flexibility) while internal traffic hits the brokers directly (performance, cost).
        Different security settings for external versus internal traffic even though the security protocol is the same (e.g. different set of enabled SASL mechanisms, authentication servers, different keystores, etc.)

    As such, we propose that Kafka brokers should be able to define multiple listeners for the same security protocol for binding (i.e. listeners) and sharing (i.e. advertised.listeners) so that internal, external and replication traffic can be separated if required.

- These are the definitions from the configuation file from Kafka.
	
listeners - Comma-separated list of URIs we will listen on and their protocols. Specify hostname as 0.0.0.0 to bind to all interfaces. Leave hostname empty to bind to default interface. Examples of legal listener lists: PLAINTEXT://myhost:9092,TRACE://:9091 PLAINTEXT://0.0.0.0:9092, TRACE://localhost:9093

advertised.listeners - Listeners to publish to ZooKeeper for clients to use, if different than the listeners above. In IaaS environments, this may need to be different from the interface to which the broker binds. If this is not set, the value for listeners will be used.
