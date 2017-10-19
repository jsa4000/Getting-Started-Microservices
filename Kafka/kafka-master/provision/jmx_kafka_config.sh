
#Some example JMX info add it to you kafka start up script
# sometimes located at /opt/kafka/kafka_2.11-0.10.1.0/bin/kafka-server-start.sh 

#Should retrieve local IP address
IP_ADDR=`ip route get 8.8.8.8 | awk '{print $NF; exit}'`
export KAFKA_OPTS="$KAFKA_OPTS -Djava.rmi.server.hostname=$IP_ADDR"
export KAFKA_OPTS="$KAFKA_OPTS -Dcom.sun.management.jmxremote.port=9090"
export KAFKA_OPTS="$KAFKA_OPTS -Dcom.sun.management.jmxremote.authenticate=false"
export KAFKA_OPTS="$KAFKA_OPTS -Dcom.sun.management.jmxremote.ssl=false" 