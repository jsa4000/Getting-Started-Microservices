package com.example.oauthservice.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.cache2k.Cache2kBuilder;
import org.cache2k.configuration.Cache2kConfiguration;
import org.cache2k.extra.spring.SpringCache2kCacheManager;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Data
@Slf4j
@EnableCaching
@Configuration
@ConfigurationProperties(prefix = "caching")
public class CacheConfiguration {

    @Data
    public static class CacheSpec {
        private Integer timeout;
        private Integer max = 200;
    }

    private Map<String, CacheSpec> specs;

    @Bean
    public CacheManager cacheManager() {
        SpringCache2kCacheManager manager = new SpringCache2kCacheManager("main-cache-manager")
                .defaultSetup(x -> x.entryCapacity(2000)
                        .expireAfterWrite(30, TimeUnit.MINUTES)
                        .keepDataAfterExpired(false));
        if (specs != null) {
            List<Cache2kConfiguration<?,?>> caches =
                    specs.entrySet().stream()
                            .map(entry -> buildCache(entry.getKey(), entry.getValue()))
                            .collect(Collectors.toList());
            manager.setCaches(caches);
        }
        return manager;
    }

    private Cache2kConfiguration<?,?> buildCache(String name, CacheSpec cacheSpec) {
        log.info("Cache {} specified timeout of {} sec, max of {}",name, cacheSpec.getTimeout(),cacheSpec.getMax());
        return Cache2kBuilder.forUnknownTypes()
                .name(name)
                .expireAfterWrite(cacheSpec.getTimeout(), TimeUnit.SECONDS)
                .entryCapacity(cacheSpec.getMax())
                .toConfiguration();
    }
}