# MONITORING

## Introduction

Monitoring types:

- *Blackbox monitoring* Monitoring from the outside No knowledge of how the application works internally 
   Examples: ping, HTTP request, inserting data and waiting for it to appear on dashboard 

  - Blackbox monitoring should be treated similarly to smoke tests.
  - It's good for finding when things have badly broken in an obvious way, and testing from outside your network
  - Not so good for knowing what's going on inside a system

- *Whitebox Monitoring* Complementary to blackbox monitoring. Works with information from inside your systems. 
   Can be simple things like CPU usage, down to the number of requests triggering a particular obscure codepath. 

Pulling over HTTP offers a number of advantages:

- You can run your monitoring on your laptop when developing changes.
- You can more easily tell if a target is down.
- You can manually go to a target and inspect its health with a web browser.

In case of Prometheus, pulling is slightly better than pushing, but it should not be considered a major point when considering a monitoring system.

## Components

### Prometheus

Prometheus is an open-source systems monitoring and alerting toolkit with an active ecosystem.
The main Prometheus server runs standalone and has no external dependencies.
Prometheus uses the *WhiteBox* and *Pull* technologies (phisolophies) to get the metrics directly from the sources, .

For high availability of the Alertmanager, you can run multiple instances in a *Mesh cluster* and configure the Prometheus servers to send notifications to each of them.

#### Monitoring

The dashboard rpovided by Proometheus is primarly used for Testing pourpose.

### Alerting System

### Exporters / Applications

### Grafana Dashboard

Prometheus by itself doesn't provides enough graphical information, however it can be used for Testing pouposes, such as create querys, check target status, etc..

Grafana dashboard is a Graphical web-based application that is very sued for monitoring systems. Also, it allows to connect to different sources such as Prometheus.
Grafana also provides different types of dashboard that can be used for visualize Kafka metrics. This simplify the users to generate the querys and configure the initial dashboard to start monitorizing kafka metrics.

Dashboards could be gotten from [Grafana website](https://grafana.com/dashboards). Following are some commong dashboard for starting.

- [Node Exporter Single Server](https://grafana.com/dashboards/22)
- [cadvisro grafana dashboard (Docker-monitoring)](https://grafana.com/dashboards/193)
- [Node Exporter Server Metrics](https://grafana.com/dashboards/405)


http://dockerhost:9090
 http://dockerhost:3000
 



 https://dzone.com/articles/monitoring-using-spring-boot-2-prometheus-and-graf