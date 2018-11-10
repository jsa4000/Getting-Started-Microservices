package com.example.kafkaconsumer.service;

import com.example.kafkaconsumer.component.JsonParser;
import com.example.kafkaconsumer.event.base.Event;
import com.example.kafkaconsumer.event.enums.UserEvent;
import com.example.kafkaconsumer.event.role.RoleCreated;
import com.example.kafkaconsumer.event.role.RoleDeleted;
import com.example.kafkaconsumer.event.role.RoleModified;
import lombok.extern.slf4j.Slf4j;
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

    public static final String HEADER_MESSAGE_VERSION = "custom-message-version";
    public static final String HEADER_ORIGIN_REALM = "custom-origin-realm";

    @KafkaListener(topics = "${application.topic.roles}")
    public void onRoleEvent(@Payload String data,
                        @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                        @Headers MessageHeaders messageHeaders,
                        Acknowledgment acknowledgment) {

        log.info("Message Received from topic {}: '{}'", topic, data);
        if (log.isDebugEnabled()) logHeaders(messageHeaders);

        if (!messageHeaders.containsKey(HEADER_MESSAGE_VERSION)) {
            log.error("Event does not contains version header [skipped]: {}", data);
        } else if (Event.VERSION.equals(messageHeaders.get(HEADER_MESSAGE_VERSION).toString())) {
            Event event = JsonParser.deserialize(data, Event.class);
            UserEvent eventType = UserEvent.valueOf(event.getMessageType());
            switch (eventType) {
                case CREATED:
                    if (event.getMessageType().equals(RoleCreated.VERSION)) {
                        RoleCreated message = JsonParser.deserialize(event.getMessage(),RoleCreated.class);
                        log.debug(message.toString());
                    } else {
                        log.error("Message version is not compatible [skipped]: {} vs {}",
                                event.getMessageType(), RoleCreated.VERSION);
                    }
                    break;
                case MODIFIED:
                    if (event.getMessageType().equals(RoleModified.VERSION)) {
                        RoleModified message = JsonParser.deserialize(event.getMessage(),RoleModified.class);
                        log.debug(message.toString());
                    } else {
                        log.error("Message version is not compatible [skipped]: {} vs {}",
                                event.getMessageType(), RoleModified.VERSION);
                    }
                    break;
                case DELETED:
                    if (event.getMessageType().equals(RoleDeleted.VERSION)) {
                        RoleDeleted message = JsonParser.deserialize(event.getMessage(),RoleDeleted.class);
                        log.debug(message.toString());
                    } else {
                        log.error("Message version is not compatible [skipped]: {} vs {}",
                                event.getMessageType(), RoleDeleted.VERSION);
                    }
                    break;
            }
        } else {
            log.error("Event version is not compatible [skipped]: {} vs {}",
                    messageHeaders.get(HEADER_MESSAGE_VERSION).toString(), Event.VERSION);
        }
        acknowledgment.acknowledge();
    }

    @KafkaListener(topics = "${application.topic.users}")
    public void onUserEvent(@Payload String data,
                        @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                        @Headers MessageHeaders messageHeaders,
                        Acknowledgment acknowledgment) {

        log.info("Received from topic {}: '{}'", topic, data);
        if (log.isDebugEnabled()) logHeaders(messageHeaders);

        if (!messageHeaders.containsKey(HEADER_MESSAGE_VERSION)) {
            log.error("Event does not contains version header [skipped]: {}", data);
        } else if (Event.VERSION.equals(messageHeaders.get(HEADER_MESSAGE_VERSION).toString())) {
            Event event = JsonParser.deserialize(data, Event.class);
            UserEvent eventType = UserEvent.valueOf(event.getMessageType());
            switch (eventType) {
                case CREATED:
                    if (event.getMessageType().equals(RoleCreated.VERSION)) {
                        RoleCreated message = JsonParser.deserialize(event.getMessage(),RoleCreated.class);
                        log.debug(message.toString());
                    } else {
                        log.error("Message version is not compatible [skipped]: {} vs {}",
                                event.getMessageType(), RoleCreated.VERSION);
                    }
                    break;
                case MODIFIED:
                    if (event.getMessageType().equals(RoleModified.VERSION)) {
                        RoleModified message = JsonParser.deserialize(event.getMessage(),RoleModified.class);
                        log.debug(message.toString());
                    } else {
                        log.error("Message version is not compatible [skipped]: {} vs {}",
                                event.getMessageType(), RoleModified.VERSION);
                    }
                    break;
                case DELETED:
                    if (event.getMessageType().equals(RoleDeleted.VERSION)) {
                        RoleDeleted message = JsonParser.deserialize(event.getMessage(),RoleDeleted.class);
                        log.debug(message.toString());
                    } else {
                        log.error("Message version is not compatible [skipped]: {} vs {}",
                                event.getMessageType(), RoleDeleted.VERSION);
                    }
                    break;
            }
        } else {
            log.error("Event version is not compatible [skipped]: {} vs {}",
                    messageHeaders.get(HEADER_MESSAGE_VERSION).toString(), Event.VERSION);
        }
        acknowledgment.acknowledge();
    }

    private void logHeaders(MessageHeaders messageHeaders) {
        messageHeaders.keySet()
                .forEach(key -> log.debug("{}: {}", key, messageHeaders.get(key)));
    }

}
