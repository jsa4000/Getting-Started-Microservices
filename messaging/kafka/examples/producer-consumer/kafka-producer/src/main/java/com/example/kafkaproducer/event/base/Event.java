package com.example.kafkaproducer.event.base;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@ToString
@NoArgsConstructor
public class Event <T extends EventBase> {

    private final String VERSION = "1.0.0";

    @Getter
    protected String id = UUID.randomUUID().toString();
    @Getter
    protected long timestamp = Instant.now().toEpochMilli();
    @Getter
    protected String version;
    @Getter
    protected String messageType;
    @Getter
    protected String messageVersion;
    @Getter
    protected T message;

    public Event (T message) {
        this.message = message;
        this.messageVersion = message.version;
        this.messageType = message.type;
    }

}
