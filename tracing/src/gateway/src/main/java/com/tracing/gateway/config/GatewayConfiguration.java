package com.tracing.gateway.config;

import brave.Tracing;
import brave.opentracing.BraveTracer;
import com.tracing.gateway.controller.GatewayController;
import io.jaegertracing.Configuration.ReporterConfiguration;
import io.jaegertracing.Configuration.SamplerConfiguration;
import io.jaegertracing.Configuration.SenderConfiguration;
import io.jaegertracing.internal.samplers.ConstSampler;
import io.opentracing.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.okhttp3.OkHttpSender;
import brave.sampler.Sampler;

@Configuration
public class GatewayConfiguration {

    private final Logger logger = LoggerFactory.getLogger(GatewayController.class);

    private final String OPEN_TRACING_PROVIDER = "OPEN_TRACING_PROVIDER";
    private final String JAEGER_TRACING = "JAEGER";
    private final String ZIPKIN_TRACING = "ZIPKIN";

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder.build();
    }

    @Bean
    public Tracer openTracer() {
        String provider = System.getenv(OPEN_TRACING_PROVIDER);
        if (provider == null) provider = JAEGER_TRACING;
        switch (provider) {
            case JAEGER_TRACING: 
                return jaegerTracer();
            case ZIPKIN_TRACING:  
                return zipkinTracer();
            default:
                return jaegerTracer();
        }
    }

    public Tracer jaegerTracer() {
        logger.info("Using JAEGER Provider for OpenTracing Implementation");
        return new io.jaegertracing.Configuration("gateway-service" )
                .withSampler(new SamplerConfiguration().withType(ConstSampler.TYPE).withParam(1))
                .withReporter(new ReporterConfiguration().withLogSpans(true)
                        .withSender(new SenderConfiguration().withAgentHost("10.0.0.10").withAgentPort(6831)))
                .getTracer();
    }

    public Tracer zipkinTracer() {
        logger.info("Using ZIPKIN Provider for OpenTracing Implementation");
        OkHttpSender sender = OkHttpSender.create("http://10.0.0.10:9412/api/v2/spans");
        AsyncReporter reporter = AsyncReporter.create(sender);

        Tracing braveTracer = Tracing.newBuilder()
                .localServiceName("gateway-service")
                .spanReporter(reporter)
                .traceId128Bit(true)
                .sampler(Sampler.ALWAYS_SAMPLE)
                .build();

        return BraveTracer.create(braveTracer);
    }
}
