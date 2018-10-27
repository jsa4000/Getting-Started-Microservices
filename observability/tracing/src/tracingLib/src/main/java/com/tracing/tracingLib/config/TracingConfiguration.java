package com.tracing.tracingLib.config;

import brave.Tracing;
import brave.opentracing.BraveTracer;
import brave.sampler.Sampler;
import io.jaegertracing.Configuration.ReporterConfiguration;
import io.jaegertracing.Configuration.SamplerConfiguration;
import io.jaegertracing.Configuration.SenderConfiguration;
import io.jaegertracing.internal.samplers.ConstSampler;
import io.opentracing.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.okhttp3.OkHttpSender;

@Configuration
public class TracingConfiguration {

    private final Logger logger = LoggerFactory.getLogger(TracingConfiguration.class);

    @Value("${spring.application.name}")
    private String serviceName;

    @Value("${tracing.provider:JAEGER}")
    private String tracingProvider;

    @Value("${tracing.provider.jaeger.host}")
    private String jaegerHost;

    @Value("${tracing.provider.jaeger.port}")
    private Integer jaegerPort;

    @Value("${tracing.provider.zipkin.url}")
    private String zipkinUrl;

    @Bean
    public Tracer openTracer() {
        switch (TracingProvider.valueOf(tracingProvider)) {
            case JAEGER:
                return jaegerTracer();
            case ZIPKIN:
                return zipkinTracer();
            default:
                return jaegerTracer();
        }
    }

    public Tracer jaegerTracer() {
        logger.info("Using JAEGER Provider for OpenTracing");
        return new io.jaegertracing.Configuration(serviceName)
                .withSampler(new SamplerConfiguration().withType(ConstSampler.TYPE).withParam(1))
                .withReporter(new ReporterConfiguration().withLogSpans(true)
                        .withSender(new SenderConfiguration().withAgentHost(jaegerHost).withAgentPort(jaegerPort)))
                .getTracer();
    }

    public Tracer zipkinTracer() {
        logger.info("Using ZIPKIN Provider for OpenTracing");
        OkHttpSender sender = OkHttpSender.create(zipkinUrl);
        AsyncReporter reporter = AsyncReporter.create(sender);

        Tracing braveTracer = Tracing.newBuilder()
                .localServiceName(serviceName)
                .spanReporter(reporter)
                .traceId128Bit(true)
                .sampler(Sampler.ALWAYS_SAMPLE)
                .build();

        return BraveTracer.create(braveTracer);
    }
}
