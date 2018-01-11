# Installation Instructions



## 1. Zookeeper

 - Number of Zookeepers (odd number is recommended 3,5,..) must ensure the minimun **quorum**. This is defined by the parameter syncLimit.
	-> This means that in a three-node ensemble, you can run with onenode missing. With a five-node ensemble, you can run with two nodes missing, and so on.
 - Create at least 2 Zookeeper ensembles (2 Cluster/Racks separated). For each cluster we create 3 Zookepers.
 
 - Following the configuation of the Zookeper:
 
	- Id of the zookeper in the ensemble.
	- Port the clients use to connect.
	- Ports configured for leader and follower mode for each zookeper (zookeeper1:22888:23888)
	- Parameters such as tick_time, init_time, unc_limit, etc..s
 
 zookeeper1:
    image: confluentinc/cp-zookeeper:3.3.0
    networks:
      - mynetwork
    ports:
      - "22181:22181"
      - "22888:22888"
      - "23888:23888"
    environment:
      ZOOKEEPER_SERVER_ID: 1
      ZOOKEEPER_CLIENT_PORT: 22181
      ZOOKEEPER_TICK_TIME: 2000  # Define the milliseconds of a tick (to use with init_limit)
      ZOOKEEPER_INIT_LIMIT: 5	 # Amount of time to allow followers to connect with a leader. (5 ticks at 2000 milleseconds a tick, or 10 seconds)
      ZOOKEEPER_SYNC_LIMIT: 2    # Limits how out-of-sync followers can be with the leader.
      ZOOKEEPER_SERVERS: zookeeper1:22888:23888;zookeeper2:32888:33888;zookeeper3:42888:43888;10.0.0.12:52888:53888;10.0.0.13:62888:63888


## 2. Kafka


- Create 4 Kafka Brokers (2 Cluster/Racks).

he essential configurations are the following:

    broker.id
    log.dirs
    zookeeper.connect 
	
log.dirs: This is a comma-separated list of paths on the local system.

The first is that all brokers must have the same configuration for the zookeeper.connect parameter. This specifies the Zookeeper ensemble and path where the cluster stores metadata. The second requirement is that all brokers in the cluster must have a unique value for the broker.id parameter'

  kafka:
    image: wurstmeister/kafka:0.11.0.1
    depends_on:
     - zookeeper1
     - zookeeper2
     - zookeeper3
    networks:
      - mynetwork
    ports:
      - "9092:9092"
      - "9992:9992"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_BROKER_RACK: 1
      KAFKA_JMX_OPTS: "-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Djava.rmi.server.hostname=10.0.0.11 -Dcom.sun.management.jmxremote.rmi.port=9992"
      JMX_PORT: 9992
      KAFKA_ADVERTISED_HOST_NAME: 10.0.0.11
      KAFKA_ADVERTISED_PORT: 9092
      KAFKA_ZOOKEEPER_CONNECT: zookeeper1:22181,zookeeper2:32181,zookeeper3:42181
      KAFKA_AUTO_CREATE_TOPICS_ENABLE: 'false'
    volumes:
      - /var/run/docker.sock:/var/run/docker.sock
	  
	  
	  
	  KAFKA_LOG_DIRS: /kafka
      KAFKA_BROKER_ID: 1
      KAFKA_CREATE_TOPICS: test-topic-1:1:2,test-topic-2:1:2,test-topic-3:1:2
      KAFKA_ADVERTISED_HOST_NAME: 192.168.99.100
      KAFKA_ADVERTISED_PORT: 9092
      KAFKA_LOG_RETENTION_HOURS: "168"
      KAFKA_LOG_RETENTION_BYTES: "100000000"
KAFKA_ZOOKEEPER_CONNECT: zoo1:2181,zoo2:2181,zoo3:2181