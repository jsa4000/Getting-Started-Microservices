package com.example.gateway.config.bean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import java.util.List;
import java.util.stream.Collectors;

@Primary
@Component
@EnableAutoConfiguration
public class SwaggerAggregator implements SwaggerResourcesProvider {

    public static final boolean INCLUDE_SWAGGER_OAUTH_API = true;

    @Autowired
    ZuulProperties zuulProperties;

    @Override
    public List<SwaggerResource> get() {
        List<SwaggerResource> resources = zuulProperties.getRoutes()
                .keySet().stream().map(service -> {
            SwaggerResource swaggerResource = new SwaggerResource();
            swaggerResource.setName(service);
            swaggerResource.setLocation(String.format("/api/%s/v2/api-docs",service));
            swaggerResource.setSwaggerVersion("2.0");
            return swaggerResource;
        }).collect(Collectors.toList());

        if (INCLUDE_SWAGGER_OAUTH_API) {
            SwaggerResource swaggerResource = new SwaggerResource();
            swaggerResource.setName("OAuth");
            swaggerResource.setLocation("/v2/api-docs");
            swaggerResource.setSwaggerVersion("2.0");
            resources.add(swaggerResource);
        }

        return resources;
    }
}