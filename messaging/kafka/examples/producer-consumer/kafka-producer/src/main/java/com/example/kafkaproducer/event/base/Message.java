package com.example.kafkaproducer.event.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    @Getter
    @JsonIgnore
    protected String key;

    @Getter
    @JsonIgnore
    protected String version;

    @Getter
    @JsonIgnore
    protected String type;

}
