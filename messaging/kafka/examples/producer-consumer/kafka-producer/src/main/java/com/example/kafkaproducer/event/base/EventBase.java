package com.example.kafkaproducer.event.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.ToString;

@ToString
public class EventBase {

    @Getter
    @JsonIgnore
    protected String version;

    @Getter
    @JsonIgnore
    protected String type;

}
