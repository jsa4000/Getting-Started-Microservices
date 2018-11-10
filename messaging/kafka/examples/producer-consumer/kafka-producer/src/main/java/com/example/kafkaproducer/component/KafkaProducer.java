package com.example.kafkaproducer.component;

import com.example.kafkaproducer.event.base.Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Slf4j
@Component
public class KafkaProducer {

    public static final String HEADER_EVENT_VERSION = "custom-event-version";
    public static final String HEADER_ORIGIN_REALM = "custom-origin-realm";

    @Value("${spring.application.name}")
    private String serviceName;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void publish(Event event, String topic) {
        String payload = JsonParser.serialize(event);
        log.info("Begin Publish Message: {}", payload);

        Message<String> message = MessageBuilder
                .withPayload(payload)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .setHeader(KafkaHeaders.MESSAGE_KEY, event.getKey())
                .setHeader(KafkaHeaders.TIMESTAMP, event.getTimestamp())
                .setHeader(HEADER_EVENT_VERSION, event.getVersion())
                .setHeader(HEADER_ORIGIN_REALM, serviceName)
                .build();

        kafkaTemplate.send(message).addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
            @Override
            public void onSuccess(final SendResult<String, String> message) {
                log.info("Message Published: " + message + " with offset= " + message.getRecordMetadata().offset());
            }

            @Override
            public void onFailure(final Throwable throwable) {
                log.error("Failed to Publish Message: " + message, throwable);
            }
        });
    }

}
