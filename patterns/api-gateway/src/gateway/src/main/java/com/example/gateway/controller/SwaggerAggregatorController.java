package com.example.gateway.controller;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Primary
@Component
@EnableAutoConfiguration
public class SwaggerAggregatorController implements SwaggerResourcesProvider {
    @Override
    public List<SwaggerResource> get() {
        List<String> services = Arrays.asList("customer","catalog","auth");
        List<SwaggerResource> resources = services.stream().map(service -> {
            SwaggerResource swaggerResource = new SwaggerResource();
            swaggerResource.setName(service);
            swaggerResource.setLocation(String.format("/api/%s/v2/api-docs",service));
            swaggerResource.setSwaggerVersion("2.0");
            return swaggerResource;
        }).collect(Collectors.toList());
        return resources;
    }
}