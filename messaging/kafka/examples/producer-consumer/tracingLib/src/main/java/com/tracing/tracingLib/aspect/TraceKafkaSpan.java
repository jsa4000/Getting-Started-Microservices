package com.tracing.tracingLib.aspect;

import com.tracing.tracingLib.helpers.KafkaTools;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.messaging.MessageHeaders;

import java.util.Arrays;
import java.util.Optional;

@Slf4j
@Aspect
public class TraceKafkaSpan {

    @Before("@annotation(com.tracing.tracingLib.annotation.TraceKafkaSpan)")
    public void extractSpanFromHeaders(JoinPoint joinPoint) throws Throwable {
        Optional<Object> headers = Arrays.stream(joinPoint.getArgs())
                .filter(arg -> arg instanceof MessageHeaders).findFirst();
        if (headers.isPresent()) {
            log.debug("Processing Span from Kafka Headers");
            KafkaTools.setSpanContext((MessageHeaders)headers.get());
        }
    }

}
