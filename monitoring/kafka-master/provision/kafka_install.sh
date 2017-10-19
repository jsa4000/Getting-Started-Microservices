#!/usr/bin/env bash

echo "Create new User for kafka"
useradd kafka -m

echo "Update and Install Java JRE & JDK"
apt-get update
apt-get install -y default-jdk
  
echo "Install Zookeeper Demon and Download kafka" 
apt-get install -y zookeeperd
wget http://ftp.cixug.es/apache/kafka/0.11.0.1/kafka_2.11-0.11.0.1.tgz

echo "Install Kafka (unzip)" 
tar xvf kafka_2.11-0.11.0.1.tgz
mv kafka_2.11-0.11.0.1 /usr/local/kafka

