# Installation Instructions

- Create the vagrant intance. This will install the necessary Packages to run Docker and Docker-compose

      vagrant up

> Remeber the default box created runs at 10.0.0.10. Some configuration uses this IP address, so don't forget to change it.

- Open **Openshell** terminals and connect to the curren VM instance:

      vagrant ssh

- Copy the content recursively into the current Home directory:

      cp -r /vagrant/deployments/* .

- Start using one the terminals the Kafka Cluster with the Zookeper, kafka broker and kafka-exporter for the metrics.

      sudo docker-compose -f docker-compose-kafka-jmx-exporter.yml up

> The first time docker-engine will fetch ad download all the images.

- Next start the Monitoring Cluster with GRafana, Prometheus, CAdvisor and Node-exporter.

      sudo docker-compose up

> Same as before this will fetch all the images firstly.
> In this case cadvisor check and node must be deployed in evey node since te collect metrics for each system. In this example both are deployed in the same cluster s grafana. 
> The way metrics are registered into prometheus are static (configuration file), however it can be used Servide Discovery, Kubernetes, etc. to register new Targetes.

NOTE: All this process works at the current date, however it could be errors due some changes in newer versions. Be carefu and take a look at the messages when the deployment of the containers.

- Open Following URLs and configure graphana with custmized Dashboards:

```yml
- Virtual Machines:

 -title:  "Kafka Metrics (10.0.0.10:7071)"
  uri:    "http://10.0.0.10:7071/"

 -title:   "Prometheus (10.0.0.10:9090)" 
  uri:     "http://10.0.0.10:9090/graph"

 -title:  "cAdvisor (10.0.0.10:8080)"
  uri:    "http://10.0.0.10:8080/containers/"

 -title:  "Node Exporter (10.0.0.10:9100)"
   url:    "http://10.0.0.10:9100/"

 -title:  "Grafana (10.0.0.10:3000)"
  uri:    "http://10.0.0.10:3000/login"
 ```

- Start the Kafka Producer to start sending messasges to the brokers:
  Use DRE and DRECallGenerator to generate CDR for billing.
 
> Be sure -broker-list are updated accordingly with the Clusters created

- Create a Basic Consumer to start fetching messages from Kafka partitions:

      sudo docker run -it wurstmeister/kafka:0.11.0.1 bash
      kafka-topics.sh --list --zookeeper 10.0.0.10:2181
      kafka-topics.sh --describe --zookeeper 10.0.0.10:2181
      kafka-console-consumer.sh --bootstrap-server 10.0.0.10:9092 --topic OCS-CdrEvent --from-beginning


- Finally check all it's working properly and the metrics are updated.

> Sometimes because the VM and the RAM used for virtualization is not enough for GRafana and the containers to run smoohly.