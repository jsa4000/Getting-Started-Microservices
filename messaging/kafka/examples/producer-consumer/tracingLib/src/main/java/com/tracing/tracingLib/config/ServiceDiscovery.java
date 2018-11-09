package com.tracing.tracingLib.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceDiscovery {

    private final Logger logger = LoggerFactory.getLogger(ServiceDiscovery.class);

    @Value("${servers.gateway:localhost:8080}")
    private String gatewayServerAddress;

    @Value("${servers.server1:localhost:8081}")
    private String server1Address;

    @Value("${servers.server11:localhost:8082}")
    private String server11Address;

    @Value("${servers.server12:localhost:8083}")
    private String server12Address;

    @Value("${servers.server2:localhost:8084}")
    private String server2Address;

    public String getGatewayServerAddress() {
        return gatewayServerAddress;
    }
    public String getServer1Address() {
        return server1Address;
    }
    public String getServer11Address() {
        return server11Address;
    }
    public String getServer12Address() {
        return server12Address;
    }
    public String getServer2Address() {
        return server2Address;
    }

}
