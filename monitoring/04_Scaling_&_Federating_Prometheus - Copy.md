# Scaling and Federating Prometheus

A single *Prometheus server* can easily handle millions of time series. That’s enough for a thousand servers with a thousand time series each scraped every 10 seconds. As your systems scale beyond that, Prometheus can scale too.

 
# 1. Initial Deployment

When *starting* out it’s best to keep things *simple*. A *single* Prometheus server per datacenter or similar failure domain (e.g. EC2 region) can typically handle a *thousand* servers, so should last you for a good while. Running one per datacenter avoids having the internet or WAN links on the critical path of your monitoring.

If you’ve more than one datacenter, you may wish to have global *aggregates* of some time series. This is done with a *“global Prometheus”* server, which *federates* from the datacenter Prometheus servers.

	- scrape_config:
	  - job_name: dc_prometheus
		honor_labels: true
		metrics_path: /federate
		params:
		  match[]:
			- '{__name__=~"^job:.*"}'   # Request all job-level time series
		static_configs:
		  - targets:
			- dc1-prometheus:9090
			- dc2-prometheus:9090

It’s suggested to run *two global Prometheus* in different datacenters. This keeps your global monitoring working even if one datacenter has an outage.

 
# 2. Splitting By Use

As you grow you’ll come to a point where a single Prometheus isn’t quite enough. The next step is to run multiple Prometheus servers per datacenter. Each one will own monitoring for some team or slice of the stack. A first pass may result in fronted, backend and machines (node exporter) for example.

As you continue to grow, this process can be repeated. MySQL and Cassandra monitoring may end up with their own Prometheus, or each Cassandra cluster may have a Prometheus server dedicated to it.

You may also wish to start splitting by use before there are performance issues, as teams may not want to share Prometheus or to improve isolation.

 
# 3. Horizontal Sharding

When you *can’t* subdivide Prometheus servers any longer, the final step in scaling is to *scale out*. This usually requires that a single job has thousands of instances, a scale that most users never reach. This is more complex setup and is much more involved to manage than a normal Prometheus deployment, so should be avoided for as long as you can.

The architecture is to have multiple *slave* Prometheus, each *scraping* a subset of the *targets* and *aggregating* them up within the slave. 

A master *federates* the *aggregates* produced by the *slaves*, and then the *master* *aggregates* them up to the *job* level.

On the slaves you can use a hash of the address to select only some targets to scrape:

	global:
	  external_labels:
		slave: 1  # This is the 2nd slave. This prevents clashes between slaves.
	scrape_configs:
	  - job_name: some_job
		# Add usual service discovery here, such as static_configs
		relabel_configs:
		- source_labels: [__address__]
		  modulus:       4    # 4 slaves
		  target_label:  __tmp_hash
		  action:        hashmod
		- source_labels: [__tmp_hash]
		  regex:         ^1$  # This is the 2nd slave
		  action:        keep

And the master federates from the slaves:

	- scrape_config:
	  - job_name: slaves
		honor_labels: true
		metrics_path: /federate
		params:
		  match[]:
			- '{__name__=~"^slave:.*"}'   # Request all slave-level time series
		static_configs:
		  - targets:
			- slave0:9090
			- slave1:9090
			- slave3:9090
			- slave4:9090

Information for dashboards is usually taken from the master. If you wanted to drill down to a particular target, you’d do so via its slave.
