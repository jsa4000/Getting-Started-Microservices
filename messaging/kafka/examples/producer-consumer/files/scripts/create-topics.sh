#!/bin/bash

# Wait 2s until other containers intitialize
sleep 2s
# Create userManagement topic
kafka-topics.sh --zookeeper zookeeper:2181 --create --topic userManagement --partitions 2 --replication-factor 1
# Create roleManagement topic
kafka-topics.sh --zookeeper zookeeper:2181 --create --topic roleManagement --partitions 2 --replication-factor 1

