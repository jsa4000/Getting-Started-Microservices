
### How a consumer be subscribed to more that one topic?

Yes, one consumer can be subscribed to a more than one Topic.

### How to ensure ordering in Kafka Topics for Consumer?

Kafka only provides a total order over messages within a partition, not between different partitions in a topic. Per-partition ordering combined with the ability to partition data by key is sufficient for most applications. However, if you require a total order over messages this can be achieved with a topic that has only one partition, though this will mean only one consumer process per consumer group.

Partitions increase parallelism of Kafka topic. Any number of consumers/producers can use the same partition. Its up to application layer to define the protocol. **Kafka guarantees delivery. **

### How are partitions numbered? 

Partitions start from 0 to the total number of partitions configured for the Topic.


### What exactly relationship between a key and partition? 

Keys may be used to send messages to the same partition. For example hash(key)%num_partition. The logic is pluggable to Producer. https://kafka.apache.org/090/javadoc/index.html?org/apache/kafka/clients/producer/Partitioner.html


### If I have 2 partitions in a topic and want some particular messages go to one partition and other messages go to another I should use a specific key for one specific partition, and the rest for another?

Yes. but be careful not to end up with some key that will result in the "dedicated" partition. For this, you may want to have dedicated topic. For example, control topic and data topic

### How in general I send messages to a particular partition in order to know for a consumer from where to read? Or I better off with multiple topics?

I believe consumers should not make assumptions of the data based on partition. The typical approach is to have consumer group that can read from multiple partitions of a topic. If you want to have dedicated channels, it is better (safer/maintainable) to use separate topics.

### How does Kafka depend on Zookeeper?

Starting from 0.9, we are removing all the Zookeeper dependency from the clients (for details one can check this page). However, the brokers will continue to be heavily depend on Zookeeper for:

    Server failure detection.
    Data partitioning.
    In-sync data replication. (ISR)

Once the Zookeeper quorum is down, brokers could result in a bad state and could not normally serve client requests, etc. Although when Zookeeper quorum recovers, the Kafka brokers should be able to resume to normal state automatically, there are still a few corner cases the they cannot and a hard kill-and-recovery is required to bring it back to normal. Hence it is recommended to closely monitor your zookeeper cluster and provision it so that it is performant.

Also note that if Zookeeper was hard killed previously, upon restart it may not successfully load all the data and update their creation timestamp. To resolve this you can clean-up the data directory of the Zookeeper before restarting (if you have critical metadata such as consumer offsets you would need to export / import them before / after you cleanup the Zookeeper data and restart the server).

### How many topics can I have?

Unlike many messaging systems Kafka topics are meant to scale up arbitrarily. Hence we encourage fewer large topics rather than many small topics. So for example if we were storing notifications for users we would encourage a design with a single notifications topic partitioned by user id rather than a separate topic per user.

The actual scalability is for the most part determined by the number of total partitions across all topics not the number of topics itself (see the question below for details).


### How do I choose the number of partitions for a topic?

In general, more partitions in a Kafka cluster leads to higher throughput. However, one does have to be aware of the potential impact of having too many partitions in total or per broker on things like availability and latency. In the future, we do plan to improve some of those limitations to make Kafka more scalable in terms of the number of partitions.

There isn't really a right answer, we expose this as an option because it is a tradeoff. The simple answer is that the partition count determines the maximum consumer parallelism and so you should set a partition count based on the maximum consumer parallelism you would expect to need (i.e. over-provision). Clusters with up to 10k total partitions are quite workable. Beyond that we don't aggressively test (it should work, but we can't guarantee it).

Here is a more complete list of tradeoffs to consider:

    A partition is basically a directory of log files.
    Each partition must fit entirely on one machine. So if you have only one partition in your topic you cannot scale your write rate or retention beyond the capability of a single machine. If you have 1000 partitions you could potentially use 1000 machines.
    Each partition is totally ordered. If you want a total order over all writes you probably want to have just one partition.
    Each partition is not consumed by more than one consumer thread/process in each consumer group. This allows to have each process consume in a single threaded fashion to guarantee ordering to the consumer within the partition (if we split up a partition of ordered messages and handed them out to multiple consumers even though the messages were stored in order they would be processed out of order at times).
    Many partitions can be consumed by a single process, though. So you can have 1000 partitions all consumed by a single process.
    Another way to say the above is that the partition count is a bound on the maximum consumer parallelism.
    More partitions will mean more files and hence can lead to smaller writes if you don't have enough memory to properly buffer the writes and coalesce them into larger writes
    Each partition corresponds to several znodes in zookeeper. Zookeeper keeps everything in memory so this can eventually get out of hand.
    More partitions means longer leader fail-over time. Each partition can be handled quickly (milliseconds) but with thousands of partitions this can add up.
    When we checkpoint the consumer position we store one offset per partition so the more partitions the more expensive the position checkpoint is.
    It is possible to later expand the number of partitions BUT when we do so we do not attempt to reorganize the data in the topic. So if you are depending on key-based semantic partitioning in your processing you will have to manually copy data from the old low partition topic to a new higher partition topic if you later need to expand.

Note that I/O and file counts are really about #partitions/#brokers, so adding brokers will fix problems there; but zookeeper handles all partitions for the whole cluster so adding machines doesn't help.

### How to replace a failed broker?

When a broker fails, Kafka doesn't automatically re-replicate the data on the failed broker to other brokers. This is because in the common case, one brings down a broker to apply code or config changes, and will bring up the broker quickly afterward. Re-replicating the data in this case will be wasteful. In the rarer case that a broker fails completely, one will need to bring up another broker with the same broker id on a new server. The new broker will automatically replicate the missing data.

### Can I add new brokers dynamically to a cluster?

Yes, new brokers can be added online to a cluster. Those new brokers won't have any data initially until either some new topics are created or some replicas are moved to them using the partition reassignment tool. (https://cwiki.apache.org/confluence/display/KAFKA/Replication+tools)

See this problem solved by a Software engineer: http://tech.gc.com/scaling-with-kafka/

### Can the number of partitions (per topic) be changed after creation?

Kafka documentation states we can’t reduce the number of partitions per topic once created. however it can be resized to be scaled to create more partitions. For this pourpose use Add Partition Tool.: bin/kafka-add-partitions.sh

### Can the replication factor of a Topic be changed after creation?

https://kafka.apache.org/documentation/#basic_ops_increase_replication_factor

To increase the number of replicas for a given topic you have to:

	1. Specify the extra replicas in a custom reassignment json file

	For example, you could create increase-replication-factor.json and put this content in it:

	{"version":1,
	  "partitions":[
		 {"topic":"signals","partition":0,"replicas":[0,1,2]},
		 {"topic":"signals","partition":1,"replicas":[0,1,2]},
		 {"topic":"signals","partition":2,"replicas":[0,1,2]}
	]}

	2. Use the file with the --execute option of the kafka-reassign-partitions tool

	[or kafka-reassign-partitions.sh - depending on the kafka package]

	For example:

	$ kafka-reassign-partitions --zookeeper localhost:2181 --reassignment-json-file increase-replication-factor.json --execute

	3. Verify the replication factor with the kafka-topics tool

	[or kafka-topics.sh - depending on the kafka package]

	 $ kafka-topics --zookeeper localhost:2181 --topic signals --describe

	Topic:signals   PartitionCount:3    ReplicationFactor:3 Configs:retention.ms=1000000000
	Topic: signals  Partition: 0    Leader: 2   Replicas: 0,1,2 Isr: 2,0,1
	Topic: signals  Partition: 1    Leader: 2   Replicas: 0,1,2 Isr: 2,0,1
	Topic: signals  Partition: 2    Leader: 2   Replicas: 0,1,2 Isr: 2,0,1


### How Consumer Groups are created?

Consumer groups are created when a subscriber with a groupId is subscribed to a certain Topic. Kafka broker will take into account this new group Id. 

In command line:

    - Default, kafka-console-consumer.sh will create a random group.
	
    - If you want to specify the group name, you can use --consumer.config or --consumer-property:
	
        1. Add group.id=group_name to a local file filename
		
			Use --consumer.config filename option of kafka-console-consumer.sh to set group
		
		2. Use --consumer-property in the script call:
		
			kafka-console-consumer.sh --bootstrap-server <brokerIP>:9092 --topic test --consumer-property group.id=test-consumer-group
		
		
    You can check your groups at zookeeper's /consumers/ directory or
	
		kafka-consumer-groups.sh --bootstrap-server <brokerIP>:9092 --describe --group test-consumer-group
	

Using source, groups are included in the consumer configuration.

	private static ConsumerConfig createConsumerConfig(String a_zookeeper, String a_groupId) {
			Properties props = new Properties();
			props.put("zookeeper.connect", a_zookeeper);
			props.put("group.id", a_groupId);
			props.put("zookeeper.session.timeout.ms", "400");
			props.put("zookeeper.sync.time.ms", "200");
			props.put("auto.commit.interval.ms", "1000");
			return new ConsumerConfig(props);
		}

### Is there any Web Management tool for Apache Kafka?

A tool for managing Apache Kafka. https://github.com/yahoo/kafka-manager	

### Can I set the latency for the Worker / Task scheduled using Kafka Connect?

This is the default config file that it will be passed through the worker to create a task

	name=wikipedia-irc-source
	connector.class=io.amient.kafka.connect.irc.IRCFeedConnector
	tasks.max=10
	irc.host=rc.wikimedia.org
	irc.port=6667
	irc.channels=#en.wikipedia,#en.wiktionary,#en.wikinews
	topic=wikipedia-raw

Some properties for Worker (overrides) and Connector are stored separately inside the connector itself:

	Properties workerProps = new Properties();
	workerProps.put(DistributedConfig.GROUP_ID_CONFIG, "wikipedia-connect");
	workerProps.put(DistributedConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
	workerProps.put(DistributedConfig.OFFSET_STORAGE_TOPIC_CONFIG, "connect-offsets");
	workerProps.put(DistributedConfig.CONFIG_TOPIC_CONFIG, "connect-configs");
	workerProps.put(DistributedConfig.STATUS_STORAGE_TOPIC_CONFIG, "connect-status");
	workerProps.put(DistributedConfig.KEY_CONVERTER_CLASS_CONFIG, "org.apache.kafka.connect.json.JsonConverter");
	workerProps.put("key.converter.schemas.enable", "false");
	workerProps.put(DistributedConfig.VALUE_CONVERTER_CLASS_CONFIG, "org.apache.kafka.connect.json.JsonConverter");
	workerProps.put("value.converter.schemas.enable", "false");
	workerProps.put(DistributedConfig.OFFSET_COMMIT_INTERVAL_MS_CONFIG, "30000");
	workerProps.put(DistributedConfig.INTERNAL_KEY_CONVERTER_CLASS_CONFIG, "org.apache.kafka.connect.json.JsonConverter");
	workerProps.put("internal.key.converter.schemas.enable", "false");
	workerProps.put(DistributedConfig.INTERNAL_VALUE_CONVERTER_CLASS_CONFIG, "org.apache.kafka.connect.json.JsonConverter");
	workerProps.put("internal.value.converter.schemas.enable", "false");

	Properties connectorProps = new Properties();
	connectorProps.put(ConnectorConfig.NAME_CONFIG, "wikipedia-irc-source");
	connectorProps.put(ConnectorConfig.CONNECTOR_CLASS_CONFIG, "io.amient.kafka.connect.irc.IRCFeedConnector");
	connectorProps.put(ConnectorConfig.TASKS_MAX_CONFIG, "10");
	connectorProps.put(IRCFeedConnector.IRC_HOST_CONFIG, "irc.wikimedia.org");
	connectorProps.put(IRCFeedConnector.IRC_PORT_CONFIG, "6667");
	connectorProps.put(IRCFeedConnector.IRC_CHANNELS_CONFIG, "#en.wikipedia,#en.wiktionary,#en.wikinews");
	connectorProps.put(IRCFeedConnector.TOPIC_CONFIG, "wikipedia-raw");

https://github.com/amient/hello-kafka-streams/blob/master/src/main/java/io/amient/examples/wikipedia/WikipediaStreamDemo.java#L57