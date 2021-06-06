# Spring Boot Zeebe Worker 

Simple Zeebe Worker using Spring Boot Starter Example. 

This Spring Boot project adds the Spring Zeebe Starter dependency which will enable our Application to connec to and send commands to the Zeebe Cluster. 
 

```
<dependency>
  <groupId>io.zeebe.spring</groupId>
  <artifactId>spring-zeebe-starter</artifactId>
  <version>${zeebe.version}</version>
</dependency>
``` 

Then we can define Zeebe Workers by using the `@ZeebeWorker` annotation (example from `DemoApplication.java`):
```
@ZeebeWorker(type = "classify")
public void classifyEmergency(final JobClient client, final ActivatedJob job) {
        // <YOUR LOGIC HERE>
}
```

If your Zeebe Cluster is remote you need to configure inside the `application.properties` file the Broker gateway address:
```
zeebe.client.broker.gatewayAddress=127.0.0.1:26500
```

> Notice that the communication between the Client and the cluster happens using [gRPC](https://grpc.io) 


