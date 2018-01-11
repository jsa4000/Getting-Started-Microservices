sudo docker run -it wurstmeister/kafka:0.11.0.1 bash

TEST KAFKA

# kafka-topics.sh --create --topic test2 --zookeeper 10.0.0.10:2181 --partitions 1 --replication-factor 1


# kafka-console-producer.sh --broker-list 10.0.0.10:9092 --topic test2


# kafka-console-consumer.sh --bootstrap-server 10.0.0.10:9092 --topic test2 --from-beginning


TEST REAL EXAMPLE
# kafka-topics.sh --list --zookeeper 10.0.0.10:2181
# kafka-topics.sh --describe --zookeeper 10.0.0.10:2181
# kafka-console-consumer.sh --bootstrap-server 10.0.0.10:9092 --topic OCS-CdrEvent --from-beginning


PERFORMANCE COMMANDS:

- Producer test performance command :

# kafka-run-class.sh org.apache.kafka.tools.ProducerPerformance –topic <topic-name> –num-records 1000000 –record-size <record-size> –producer-props bootstrap.servers=<broker-list> –throughput <value>

- Consumer test performance command:

# bin/kafka-consumer-perf-test.sh –zookeeper <zk-list> –threads 1 –topic <topic-name> –messages 1000000

Producer

Setup
# kafka-topics.sh --zookeeper 10.0.0.10:2181 --create --topic test2 --partitions 6 --replication-factor 1
# kafka-topics.sh --zookeeper 10.0.0.10:2181 --create --topic test2 --partitions 6 --replication-factor 3

Single thread, no replication

# kafka-run-class.sh org.apache.kafka.tools.ProducerPerformance --throughput=1000 --topic=test2 --num-records=20000 --record-size=200 --producer-props bootstrap.servers=10.0.0.10:9092 buffer.memory=67108864 batch.size=64000
# kafka-run-class.sh org.apache.kafka.tools.ProducerPerformance --throughput=1000 --topic=test2 --num-records=20000 --record-size=200 --producer-props bootstrap.servers=10.0.0.10:9092

Single-thread, async 3x replication

# kafktopics.sh --zookeeper 10.0.0.10:2181 --create --topic test --partitions 6 --replication-factor 3
# kafka-run-class.sh org.apache.kafka.tools.ProducerPerformance test6 50000000 100 -1 acks=1 bootstrap.servers=10.0.0.10:9092 buffer.memory=67108864 batch.size=8196

Single-thread, sync 3x replication

# kafka-run-class.sh org.apache.kafka.tools.ProducerPerformance test 50000000 100 -1 acks=-1 bootstrap.servers=10.0.0.10:9092 buffer.memory=67108864 batch.size=64000

Three Producers, 3x async replication
# kafka-run-class.sh org.apache.kafka.tools.ProducerPerformance test 50000000 100 -1 acks=1 bootstrap.servers=10.0.0.10:9092 buffer.memory=67108864 batch.size=8196

Throughput Versus Stored Data

# kafka-run-class.sh org.apache.kafka.tools.ProducerPerformance test 50000000000 100 -1 acks=1 bootstrap.servers=10.0.0.10:9092 buffer.memory=67108864 batch.size=8196

Effect of message size

for i in 10 100 1000 10000 100000;
do
echo ""
echo $i
# kafka-run-class.sh org.apache.kafka.tools.ProducerPerformance test $((1000*1024*1024/$i)) $i -1 acks=1 bootstrap.servers=10.0.0.10:9092 buffer.memory=67108864 batch.size=128000
done;

Consumer
Consumer throughput

# kafka-consumer-perf-test.sh --zookeeper 10.0.0.10:2181 --messages 50000000 --topic test --threads 1

3 Consumers

On three servers, run:
# kafka-consumer-perf-test.sh --zookeeper 10.0.0.10:2181 --messages 50000000 --topic test --threads 1

End-to-end Latency

# kafka-run-class.sh kafka.tools.TestEndToEndLatency 10.0.0.10:9092 10.0.0.10:2181 test 5000

Producer and consumer

# kafka-run-class.sh org.apache.kafka.tools.ProducerPerformance test 50000000 100 -1 acks=1 bootstrap.servers=10.0.0.10:9092 buffer.memory=67108864 batch.size=8196

# kafka-consumer-perf-test.sh --zookeeper 10.0.0.10:2181 --messages 50000000 --topic test --threads 1

