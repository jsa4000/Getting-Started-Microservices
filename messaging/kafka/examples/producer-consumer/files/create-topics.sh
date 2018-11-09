#!/bin/bash

# - Run docker container -
# docker run -it --network producerconsumer_default wurstmeister/kafka:2.11-1.1.0 bash

# Create userManagement topic
kafka-topics.sh --zookeeper zookeeper:2181 --create --topic userManagement --partitions 2 --replication-factor 1
#Create roleManagement topic
kafka-topics.sh --zookeeper zookeeper:2181 --create --topic roleManagement --partitions 2 --replication-factor 1

# - Run following command to get the topics created -
# kafka-topics.sh --list --zookeeper zookeeper:2181
# kafka-topics.sh --describe --zookeeper zookeeper:2181