package com.tracing.tracingLib.helpers;

import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMapExtractAdapter;
import io.opentracing.util.GlobalTracer;
import org.springframework.messaging.MessageHeaders;

import java.util.HashMap;
import java.util.Map;

public class KafkaTools {

    public static void setSpanContext(MessageHeaders messageHeaders) {
        Map<String,String> headers = new HashMap<>();
        for (String key : messageHeaders.keySet()) {
            if(messageHeaders.get(key) instanceof String) {
                headers.put(key, (String) messageHeaders.get(key));
            } else if (messageHeaders.get(key) instanceof byte[]) {
                try { headers.put(key, new String(((byte[])messageHeaders.get(key)), "UTF-8"));}
                catch (Exception ex) {}
            }
        }
        Tracer tracer = GlobalTracer.get();
        SpanContext spanContext = tracer.extract(Format.Builtin.TEXT_MAP, new TextMapExtractAdapter(headers));
        tracer.buildSpan("new_span").asChildOf(spanContext).startActive(true);
    }

}
