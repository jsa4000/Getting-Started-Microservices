package com.example.kafkaproducer.event.base;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@ToString
@NoArgsConstructor
public class Event <T extends EventBase> {

    @Getter @Setter
    protected String id = UUID.randomUUID().toString();
    @Getter @Setter
    protected long timestamp = Instant.now().toEpochMilli();
    @Getter @Setter
    protected String version;
    @Getter @Setter
    protected String type;
    @Getter @Setter
    protected T payload;

    public Event (T payload) {
        this.payload = payload;
        this.version = payload.version;
        this.type = payload.type;
    }

}
