sudo docker run -it wurstmeister/kafka:0.11.0.1 bash


# TEST KAFKA
# kafka-topics.sh --create --topic test2 --zookeeper 10.0.0.10:2181 --partitions 1 --replication-factor 1
# kafka-console-producer.sh --broker-list 10.0.0.10:9092 --topic test2
# kafka-console-consumer.sh --bootstrap-server 10.0.0.10:9092 --topic test2 --from-beginning
# kafka-console-consumer.sh --bootstrap-server 10.0.0.10:9092 --topic test2 --from-beginning --describe --group console_group
# kafka-console-consumer.sh --new-consumer --bootstrap-server 10.0.0.11:9092,10.0.0.12:9092 --topic test01 --property print.key=true --property key.separator="-" --from-beginning

# TEST REAL EXAMPLE

# kafka-topics.sh --create --topic OCS-CdrEvent --zookeeper 10.0.0.11:22181 --partitions 3 --replication-factor 2
# kafka-topics.sh --create --topic OCS-CdrEvent2 --zookeeper 10.0.0.11:32181 --partitions 3 --replication-factor 2

# kafka-topics.sh --list --zookeeper 10.0.0.11:32181
# kafka-topics.sh --describe --zookeeper 10.0.0.11:42181


# kafka-console-consumer.sh --bootstrap-server 10.0.0.11:9092 --topic OCS-CdrEvent --property print.key=true --property key.separator="-" --from-beginning
# kafka-console-consumer.sh --bootstrap-server 10.0.0.12:9093 --topic OCS-CdrEvent2 --from-beginning



