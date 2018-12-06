package com.example.gateway.config;

import com.example.gateway.filters.DebugRoutingFilter;
import com.example.gateway.filters.ForwardedHeaderFilter;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableZuulProxy
@EnableDiscoveryClient
public class GatewayConfig {

    @Bean
    public ForwardedHeaderFilter forwardedHeaderFilter()  {
        return new ForwardedHeaderFilter();
    }

    @Bean
    public DebugRoutingFilter preHeaderFilter()  {
        return new DebugRoutingFilter();
    }
}
