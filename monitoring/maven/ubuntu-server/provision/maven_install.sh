#!/usr/bin/env bash

echo "Install Maven version 3.5.0"
cd /opt
wget http://ftp.cixug.es/apache/maven/maven-3/3.5.0/binaries/apache-maven-3.5.0-bin.tar.gz
tar -xvzf apache-maven-3.5.0-bin.tar.gz
mv apache-maven-3.5.0 maven 

echo "Setup environment variables"
cp /vagrant/provision/mavenenv.sh /etc/profile.d/mavenenv.sh
source /etc/profile.d/mavenenv.sh





