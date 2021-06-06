package com.example.mongo.autoconfigure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.util.List;

@Configuration
public class MongoAutoConfiguration {

    @Bean
    @Primary
    public MongoCustomConversions customConversions(List<Converter> converters) {
        return new MongoCustomConversions(converters);
    }

}
