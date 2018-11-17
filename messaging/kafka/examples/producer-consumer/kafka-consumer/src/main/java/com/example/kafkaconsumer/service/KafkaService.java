package com.example.kafkaconsumer.service;

import com.example.kafkaconsumer.component.JsonParser;
import com.example.kafkaconsumer.event.base.Event;
import com.example.kafkaconsumer.event.enums.EventType;
import com.example.kafkaconsumer.event.role.RoleCreated;
import com.example.kafkaconsumer.event.role.RoleDeleted;
import com.example.kafkaconsumer.event.role.RoleModified;
import com.example.kafkaconsumer.event.user.UserCreated;
import com.example.kafkaconsumer.event.user.UserDeleted;
import com.example.kafkaconsumer.event.user.UserModified;
import com.example.kafkaconsumer.model.Fact;
import com.tracing.tracingLib.annotation.TraceKafkaSpan;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaService {

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserService userService;

    @Autowired
    private FactService factService;

    public static final String PAYLOAD_MESSAGE_TYPE = "messageType";
    public static final String PAYLOAD_MESSAGE_VERSION = "messageVersion";

    public static final String HEADER_EVENT_ID = "id";
    public static final String HEADER_EVENT_VERSION = "custom-event-version";
    public static final String HEADER_ORIGIN_REALM = "custom-origin-realm";

    @TraceKafkaSpan
    @KafkaListener(topics = "${application.topic.roles}")
    public void onRoleEvent(@Payload String data,
                            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                            @Headers MessageHeaders messageHeaders,
                            Acknowledgment acknowledgment) {
        log.info("Message Received from topic {}: '{}'", topic, data);
        if (log.isDebugEnabled()) logHeaders(messageHeaders);

        if (!messageHeaders.containsKey(HEADER_EVENT_VERSION)) {
            log.error("Event does not contains 'version' header [skipped]: {}", data);
        } else if (!Event.VERSION.equals(messageHeaders.get(HEADER_EVENT_VERSION).toString())) {
            log.error("Event version is not compatible [skipped]: {} vs {}",
                    messageHeaders.get(HEADER_EVENT_VERSION).toString(), Event.VERSION);
        } else {
            String eventId = JsonParser.getValue(HEADER_EVENT_ID, data);
            EventType messageType = EventType.valueOf(JsonParser.getValue(PAYLOAD_MESSAGE_TYPE, data));
            String messageVersion = JsonParser.getValue(PAYLOAD_MESSAGE_VERSION, data);

            if (! messageVersion.equals(messageType.getVersion())) {
                log.error("Message version is not compatible [skipped]: {} vs {}",
                        messageVersion, messageType.getVersion());
            } else {
                switch (messageType) {
                    case ROLE_CREATED:
                        Event<RoleCreated> roleCreated = JsonParser.deserialize(data, Event.class , RoleCreated.class);
                        log.debug(roleCreated.toString());
                        roleService.save(roleCreated.getMessage().getRole());
                        break;
                    case ROLE_MODIFIED:
                        Event<RoleModified> roleModified  = JsonParser.deserialize(data, Event.class ,RoleModified.class);
                        log.debug(roleModified.toString());
                        roleService.save(roleModified.getMessage().getRole());
                        break;
                    case ROLE_DELETED:
                        Event<RoleDeleted> roleDelete  = JsonParser.deserialize(data, Event.class , RoleDeleted.class);
                        log.debug(roleDelete.toString());
                        roleService.delete(roleDelete.getMessage().getId());
                        break;
                }
            }
            factService.save(new Fact(eventId,data,true));
        }
        acknowledgment.acknowledge();
    }

    @TraceKafkaSpan
    @KafkaListener(topics = "${application.topic.users}")
    public void onUserEvent(@Payload String data,
                            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                            @Headers MessageHeaders messageHeaders,
                            Acknowledgment acknowledgment) {
        log.info("Received from topic {}: '{}'", topic, data);
        if (log.isDebugEnabled()) logHeaders(messageHeaders);

        if (!messageHeaders.containsKey(HEADER_EVENT_VERSION)) {
            log.error("Event does not contains 'version' header [skipped]: {}", data);
        } else if (!Event.VERSION.equals(messageHeaders.get(HEADER_EVENT_VERSION).toString())) {
            log.error("Event version is not compatible [skipped]: {} vs {}",
                    messageHeaders.get(HEADER_EVENT_VERSION).toString(), Event.VERSION);
        } else {
            String eventId = JsonParser.getValue(HEADER_EVENT_ID, data);
            EventType messageType = EventType.valueOf(JsonParser.getValue(PAYLOAD_MESSAGE_TYPE, data));
            String messageVersion = JsonParser.getValue(PAYLOAD_MESSAGE_VERSION, data);

            if (!messageVersion.equals(messageType.getVersion())) {
                log.error("Message version is not compatible [skipped]: {} vs {}",
                        messageVersion, messageType.getVersion());
            } else {
                switch (messageType) {
                    case USER_CREATED:
                        Event<UserCreated> userCreated = JsonParser.deserialize(data, Event.class , UserCreated.class);
                        log.debug(userCreated.toString());
                        userService.save(userCreated.getMessage().getUser());
                        break;
                    case USER_MODIFIED:
                        Event<UserModified> userModified  = JsonParser.deserialize(data, Event.class ,UserModified.class);
                        log.debug(userModified.toString());
                        userService.save(userModified.getMessage().getUser());
                        break;
                    case USER_DELETED:
                        Event<UserDeleted> userDelete  = JsonParser.deserialize(data, Event.class , UserDeleted.class);
                        log.debug(userDelete.toString());
                        userService.delete(userDelete.getMessage().getId());
                        break;
                }
            }
            factService.save(new Fact(eventId,data,true));
        }
        acknowledgment.acknowledge();
    }

    private void logHeaders(MessageHeaders messageHeaders) {
        messageHeaders.keySet()
                .forEach(key -> log.debug("{}: {}", key, messageHeaders.get(key)));
    }

}
