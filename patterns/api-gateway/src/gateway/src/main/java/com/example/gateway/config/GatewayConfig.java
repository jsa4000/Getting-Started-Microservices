package com.example.gateway.config;

import com.example.gateway.proxy.fallback.CustomerFallbackProvider;
import com.example.gateway.proxy.fallback.DefaultFallbackProvider;
import com.example.gateway.proxy.filter.DebugRoutingFilter;
import com.example.gateway.proxy.filter.ForwardedHeaderFilter;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableZuulProxy
@EnableDiscoveryClient
public class GatewayConfig {

    @Bean
    public CustomerFallbackProvider customerFallbackProvider() {return new CustomerFallbackProvider(); }

    @Bean
    public DefaultFallbackProvider defaultFallbackProvider() { return new DefaultFallbackProvider(); }

    @Bean
    public ForwardedHeaderFilter forwardedHeaderFilter()  {
        return new ForwardedHeaderFilter();
    }

    @Bean
    public DebugRoutingFilter preHeaderFilter()  {
        return new DebugRoutingFilter();
    }

}
