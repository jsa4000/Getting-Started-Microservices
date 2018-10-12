curl -X PUT -d @register-consul/node-exporter-service.json 10.0.0.10:8500/v1/agent/service/register
curl -X PUT -d @register-consul/cadvisor-exporter-service.json 10.0.0.10:8500/v1/agent/service/register
curl -X PUT -d @register-consul/kafka-exporter-service1.json 10.0.0.10:8500/v1/agent/service/register
curl -X PUT -d @register-consul/kafka-exporter-service2.json 10.0.0.10:8500/v1/agent/service/register
curl -X PUT -d @register-consul/kafka-exporter-service3.json 10.0.0.10:8500/v1/agent/service/register
