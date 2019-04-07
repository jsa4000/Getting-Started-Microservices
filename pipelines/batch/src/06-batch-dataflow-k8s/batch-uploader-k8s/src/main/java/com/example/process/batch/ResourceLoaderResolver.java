package com.example.process.batch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.deployer.resource.docker.DockerResourceLoader;
import org.springframework.cloud.deployer.resource.support.DelegatingResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

@Component
public class ResourceLoaderResolver {

    @Autowired
    DelegatingResourceLoader delegatingResourceLoader;

    public ResourceLoader get(String resourceName) {
        ResourceLoader resourceloader;
        if (resourceName.startsWith("docker")) {
            resourceloader =  new DockerResourceLoader();
        } else {
            resourceloader =  delegatingResourceLoader;
        }
        return resourceloader;
    }


}
