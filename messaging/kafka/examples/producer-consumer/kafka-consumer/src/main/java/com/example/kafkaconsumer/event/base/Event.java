package com.example.kafkaconsumer.event.base;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
public class Event {

    public static final String VERSION = "1.0.0";

    @Getter
    protected String key;
    @Getter
    protected String id;
    @Getter
    protected long timestamp;
    @Getter
    protected String version;
    @Getter
    protected String messageType;
    @Getter
    protected String messageVersion;
    @Getter
    protected String message;

}
