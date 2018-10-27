# Apache Kafka Monitoring

#1 Kafka monitoring

Apache Kafka is an open-sourced, fault-tolerant publish-subscribe-based messaging system developed by LinkedIn. A distributed log-service, Kafka is often used in place of traditional message brokers because of its higher throughput, scalability, reliability and replication.

Kafka's cluster-centric design offers strong durability and fault-tolerance. Since Kafka is a distributed system, topics are partitioned and replicated across multiple nodes; it has an incredible depth of technical detail when you dig deeper. With meaningful performance monitoring and prompt alerting of issues, Kafka can be a highly attractive option for data integration. Applications Manager collects all performance metrics that can help when troubleshooting Kafka issues and alerts you on those that require corrective action.

#1.1 Track System Resource Utilization

Automatically discover Kafka servers, and track the resource utilization details like memory, CPU and disk growth over time to ensure you don't run out of resources. Make sure your Apache Kafka server is up and continuously operating as expected. Get notified quickly whenever there are sudden surges in resource consumption or unusual patterns.
Kafka Memory Utilization

#1.2 Keep tabs on threads and JVM usage

Because Kafka runs in the Java Virtual Machine (JVM), it relies on Java garbage collection processes to free up memory. The more activity in your Kafka cluster, the more often the garbage collection will run. Track JVM heap sizes and ensure that started threads don’t overload the server's memory. Track thread usage with metrics like Daemon, Peak and Live Thread Count to prevent performance bottlenecks in your system.
Kafka Thread Usage

#1.3 Understand Broker, Controller and Replication Statistics

In a Kafka cluster, one of the brokers serves as the controller, which is responsible for managing the states of partitions and replicas and for performing administrative tasks like reassigning partitions. Monitor active controllers to see which broker was the controller when an issue occurred and offline partitions count to prevent service interruptions. Monitor the broker's Log flush latency - the longer it takes to flush log to disk, the more the pipeline backs up. Track under-replicated partitions to know if replication is going as fast as configured.
Kafka Replication Statistics
 
#1.4 Monitor Network and Topic Details

Get the full picture of network usage on your host, track network throughput or aggregate incoming and outgoing byte rate on your broker topics for more information as to where potential bottlenecks lie. Make informed decisions like whether or not you should enable end-to-end compression of your messages.
Kafka Network Usage

#1.5 Fix performance problems faster

Get instant notifications when there are performance issues with the components of Apache Kafka. Become aware of performance bottlenecks and find out which application is causing the excessive load. Take quick remedial actions before your end users experience issues. 


# 2. Monitoring Kafka with Prometheus 

Kafka by default implements metrics (MBeans) that can be extracted using JMX.

There are several ways that can be used for the connection to the Kafka server. 

- By overriding JMX_PORT. This port will be used to expose the information garthered by Java VM. 
  Using this way, tools such as JConsole or Visual VM can be use to connect via JMX.

- By overriding JMX_OPTS. This allows defining new options to JXM so jmx exporter for Prometheus can be used.
  By using this way Prometheus can connect to the jmx exporter to get the metrics from.

# 2.1 Monitoring Kafka with Prometheus 
  
- Donwload kafka latest-stable version:

	wget http://ftp.heanet.ie/mirrors/www.apache.org/dist/kafka/0.10.1.0/kafka_2.11-0.10.1.0.tgz
	tar -xzf kafka_*.tgz
	cd kafka_*

- Download jmx exporter for Prometheus and the kafka configuration for jmx exporter agent

	wget https://repo1.maven.org/maven2/io/prometheus/jmx/jmx_prometheus_javaagent/0.6/jmx_prometheus_javaagent-0.6.jar
	wget https://raw.githubusercontent.com/prometheus/jmx_exporter/master/example_configs/kafka-0-8-2.yml
	
*kafka-0-8-2.yml*
 
	lowercaseOutputName: true
	rules:
	- pattern : kafka.cluster<type=(.+), name=(.+), topic=(.+), partition=(.+)><>Value
	  name: kafka_cluster_$1_$2
	  labels:
		topic: "$3"
		partition: "$4"
	- pattern : kafka.log<type=Log, name=(.+), topic=(.+), partition=(.+)><>Value
	  name: kafka_log_$1
	  labels:
		topic: "$2"
		partition: "$3"
	- pattern : kafka.controller<type=(.+), name=(.+)><>(Count|Value)
	  name: kafka_controller_$1_$2
	- pattern : kafka.network<type=(.+), name=(.+)><>Value
	  name: kafka_network_$1_$2
	- pattern : kafka.network<type=(.+), name=(.+)PerSec, request=(.+)><>Count
	  name: kafka_network_$1_$2_total
	  labels:
		request: "$3"


- Start Default Zookeeper server

./bin/zookeeper-server-start.sh config/zookeeper.properties &

- Start Default kafka server using the jmx prometheus java agent downloaded

	KAFKA_OPTS="$KAFKA_OPTS -javaagent:$PWD/jmx_prometheus_javaagent-0.6.jar=7071:$PWD/kafka-0-8-2.yml" \
	  ./bin/kafka-server-start.sh config/server.properties &

If you visit http://localhost:7071/metrics you’ll see the metrics.

- Create Prometheus Server (Download and uncompress the contents)

	wget https://github.com/prometheus/prometheus/releases/download/v1.2.1/prometheus-1.2.1.linux-amd64.tar.gz
	tar -xzf prometheus-*.tar.gz
	
- Configre Prometheus to use kafka exporter previously started
	
	cd prometheus-*
	cat <<'EOF' > prometheus.yml
	global:
	 scrape_interval: 10s
	 evaluation_interval: 10s
	scrape_configs:
	 - job_name: 'kafka'
	   static_configs:
		- targets:
		  - localhost:7071
	EOF
	./prometheus

# 2.2 Grafana Dashboard

Prometheus by itself doesn't provides enough graphical information, however it can be used for Testing pouposes, such as create querys, check target status, etc..

Grafana dashboard is a Graphical web-based application that is very sued for monitoring systems. Also, it allows to connect to different sources such as Prometheus.
Grafana also provides different types of dashboard that can be used for visualize Kafka metrics. This simplify the users to generate the querys and configure the initial dashboard to start monitorizing kafka metrics.

Dashboards could be downloaded from Grafana website: https://grafana.com/dashboards

There are severals dashboard already preconfigured for kafka:

	https://grafana.com/dashboards/721
	https://grafana.com/dashboards/762
















