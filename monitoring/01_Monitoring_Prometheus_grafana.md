# Monitoring Services With Prometheus and Grafana

## 1 Prometheus

Prometheus is a free software ecosystem for monitoring and alerting, with focus on reliability and simplicity. See also prometheus overview and prometheus FAQ.

There's a few interesting features that are missing from what we have now, among others:

- Multi-dimensional data model

  Metrics have a name and several key=value pairs to better model what the metric is about. e.g. to measure varnish requests in the upload cache in eqiad we'd have a metric like http_requests_total{cache="upload",site="eqiad"}.

- Powerful query language

  Makes it able to ask complex questions, e.g. when debugging problems or drilling down for root cause during outages. From the example above, the query topk(3, sum(http_requests_total{status~="^5"}) by (cache)) would return the top 3 caches (text/upload/misc) with the most errors (status matches the regexp "^5")

- Pull metrics from targets

  Prometheus is primarily based on a pull model, in which the prometheus server has a list of targets it should scrape metrics from. The pull protocol is HTTP based and simply put, the target returns a list of "<metric> <value>". Pushing metrics is supported too, see also http://prometheus.io/docs/instrumenting/pushing/.


### 1.1 Install Prometheus 

In order to install Promteheus is neccesary to download the binaries from its Web site at https://prometheus.io/download/
> It's recommended to get the latest-stable version from the releases.

- Download binaries from the Web:

	$ wget https://github.com/prometheus/prometheus/releases/download/v1.7.2/prometheus-1.7.2.linux-amd64.tar.gz

- Unzip the content downloaded:

	$ tar -zxvf prometheus-1.7.2.linux-amd64.tar.gz
	
    where,

	-z : Decompress and extract the contents of the compressed archive created by gzip program (tar.gz extension).
	-x : Extract a tar.
	-v : Verbose output or show progress while extracting files.
	-f : Specify an archive or a tarball filename.
	-j : Decompress and extract the contents of the compressed archive created by bzip2 program (tar.bz2 extension).

- Run Prometheus from Command line. By default, Prometheus use Port number 9090

	$ cd prometheus-1.7.2.linux-amd64
	& ./prometheus &
	
> The *&* informs the shell to put the command in the background

- Verify prometheus is running correctly through url: http://localhost:9090

- Also verify default Metrics Prometheus support by default: http://localhost:9090/metrics

- Test a query using the Query language from prometheus:

	go_gc_duration_seconds{quantile="1"}
	
> Also, it could be used Docker Images from  https://hub.docker.com/r/prom/

### 1.2 Configure Prometheus

Default Prometheus config file is am yml file that contains all the configuration needed to start working with Prometheus.

Following there is an example of this *prometheus.yml*

	# my global config
	global:
	  scrape_interval:     15s # Set the scrape interval to every 15 seconds. Default is every 1 minute.
	  evaluation_interval: 15s # Evaluate rules every 15 seconds. The default is every 1 minute.
	  # scrape_timeout is set to the global default (10s).

	  # Attach these labels to any time series or alerts when communicating with
	  # external systems (federation, remote storage, Alertmanager).
	  external_labels:
		  monitor: 'codelab-monitor'

	# Load rules once and periodically evaluate them according to the global 'evaluation_interval'.
	rule_files:
	  # - "first.rules"
	  # - "second.rules"

	# A scrape configuration containing exactly one endpoint to scrape:
	# Here it's Prometheus itself.
	scrape_configs:
	  # The job name is added as a label `job=<job_name>` to any timeseries scraped from this config.
	  - job_name: 'prometheus'

		# metrics_path defaults to '/metrics'
		# scheme defaults to 'http'.

		static_configs:
		  - targets: ['localhost:9090']
		  		  
There are several ways to configure Prometheus. 

- The Static way where all configuration must be set in the config file

	scrape_configs:
	  # The job name is added as a label `job=<job_name>` to any timeseries scraped from this config.
	  - job_name: 'prometheus'

		# metrics_path defaults to '/metrics'
		# scheme defaults to 'http'.

		static_configs:
		  - targets: ['localhost:9090']
	
	  # The job name is added as a label `job=<job_name>` to any timeseries scraped from this config.
	  - job_name: 'node'

		# metrics_path defaults to '/metrics'
		# scheme defaults to 'http'.

		static_configs:
		  - targets: ['localhost:3000']

- The Dynamic ways, in which Prometheus provides different ways to add new services using: Service Discovery, Kubernetes, Consul, etc
		  
### 1.3 Metrics with Prometheus


## 2 Exporters

### 2.1 Node Exporter

Prometheus exporter for hardware and OS metrics exposed by *NIX kernels, written in Go with pluggable metric collectors.

To install node exporter is very similar the way it's done in Prometheus

- Download the Exporter from the Web or Github repository

	$ wget https://github.com/prometheus/node_exporter/releases/download/v0.14.0/node_exporter-0.14.0.linux-amd64.tar.gz

- Unzip the content downloaded:

	$ tar -zxvf node_exporter-0.14.0.linux-amd64.tar.gz

- Run current exporter from Command line.  Prometheus use Port number 9090

	$ cd node_exporter-0.14.0.linux-amd64
	& ./node_exporter &
	
### 2.2 Consul Exporter

Exporter for Consul metrics prometheus/consul_exporter
https://github.com/prometheus/consul_exporter/releases/download/v0.3.0/consul_exporter-0.3.0.linux-amd64.tar.gz

### 2.3 Prometheus Statics Exporter Configuratio 

In order to install any exporter into Prometheus, using the Static Way, it is needed to add the current job into *prometheus.yml* config file.

- Add current exporter to the final of the yml file as a *job_name*. 
- Configure the targets so Prometheus knows the address:port in order to get all the metrics from the exporter.

	  # The job name is added as a label `job=<job_name>` to any timeseries scraped from this config.
	  - job_name: 'node_exporter'

		# metrics_path defaults to '/metrics'
		# scheme defaults to 'http'.

		static_configs:
		  - targets: ['localhost:9100']
		  
- Re-start Prometheus so the configuartion will be loaded.
		  
- Verify current exporter has been added in *Targets* at Prometheus dashboard.

- Run a Query to test if matrics from the exporters are imported into Prometheus.


> In target, you define the address and Port number that are used into the componente that are going to be monitorized.		  
		  
## 3 Grafana


wget https://s3-us-west-2.amazonaws.com/grafana-releases/release/grafana-4.5.2.linux-x64.tar.gz
tar -zxvf grafana-4.5.2.linux-x64.tar.gz

- To execute 

cd grafana-4.5.2.linux-x64
cd bin
sudo ./grafana-server

> To know PId of the process to kill it: lsof -i :3000


## 4 AlertManager

### 1.1 Install AlertManager 

https://github.com/prometheus/alertmanager/releases/download/v0.8.0/alertmanager-0.8.0.linux-amd64.tar.gz


