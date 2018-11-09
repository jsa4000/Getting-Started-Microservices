#!/bin/bash

# - Run docker container -
# docker run -it --network producerconsumer_default wurstmeister/kafka:2.11-1.1.0 bash

sleep 2s
# Create userManagement topic
kafka-topics.sh --zookeeper zookeeper:2181 --create --topic userManagement --partitions 2 --replication-factor 1
# Create roleManagement topic
kafka-topics.sh --zookeeper zookeeper:2181 --create --topic roleManagement --partitions 2 --replication-factor 1

# - Run following command to get the topics created -
# kafka-topics.sh --list --zookeeper zookeeper:2181
# kafka-topics.sh --describe --zookeeper zookeeper:2181

# - Start the Producder
# kafka-console-producer.sh --broker-list kafka:9092 --topic userManagement
# kafka-console-producer.sh --broker-list kafka:9092 --topic roleManagement

# - Start the Consumer
# kafka-console-consumer.sh --bootstrap-server kafka:9092 --topic userManagement --from-beginning
# kafka-console-consumer.sh --bootstrap-server kafka:9092 --topic roleManagement --from-beginning