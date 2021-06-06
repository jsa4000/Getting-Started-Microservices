package com.example.mongo.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;

@ReadingConverter
@RequiredArgsConstructor
public class OffsetDateTimeReadConverter implements Converter<Date, OffsetDateTime> {

    private final ZoneOffset zoneOffset;

    @Override
    public OffsetDateTime convert(Date date) {
        return date.toInstant().atOffset(zoneOffset);
    }

}
