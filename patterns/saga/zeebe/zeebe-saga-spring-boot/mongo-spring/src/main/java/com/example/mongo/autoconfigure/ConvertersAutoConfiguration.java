package com.example.mongo.autoconfigure;

import com.example.mongo.converter.OffsetDateTimeReadConverter;
import com.example.mongo.converter.OffsetDateTimeWriteConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.ZoneOffset;

@Configuration
public class ConvertersAutoConfiguration {

    @Bean
    public OffsetDateTimeWriteConverter offsetDateTimeWriteConverter() {
        return new OffsetDateTimeWriteConverter();
    }

    @Bean
    public OffsetDateTimeReadConverter offsetDateTimeReadConverter() {
        return new OffsetDateTimeReadConverter(ZoneOffset.UTC);
    }

}
