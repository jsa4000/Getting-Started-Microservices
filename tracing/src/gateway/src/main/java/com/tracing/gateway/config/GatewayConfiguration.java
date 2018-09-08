package com.tracing.gateway.config;

import io.jaegertracing.Configuration.SamplerConfiguration;
import io.jaegertracing.Configuration.ReporterConfiguration;
import io.jaegertracing.Configuration.SenderConfiguration;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class GatewayConfiguration {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder.build();
    }

    //@Bean
//    public Tracer jaegerTracer() {
//        Tracer result =   new io.jaegertracing.Configuration("gateway-service" )
//                .withSampler(new SamplerConfiguration().withType("const").withParam(1))
//                .withReporter(new ReporterConfiguration()
//                        .withSender(new SenderConfiguration().withAgentHost("10.0.0.10").withAgentPort(5778)))
//                .getTracer();
//        GlobalTracer.register(result);
//        return result;
//    }
//
//    @Bean
//    public io.opentracing.Tracer zipkinTracer() {
//        OkHttpSender okHttpSender = OkHttpSender.builder()
//                .encoding(Encoding.JSON)
//                .endpoint("http://localhost:9411/api/v1/spans")
//                .build();
//        AsyncReporter<Span> reporter = AsyncReporter.builder(okHttpSender).build();
//        Tracing braveTracer = Tracing.newBuilder()
//                .localServiceName("spring-boot")
//                .reporter(reporter)
//                .traceId128Bit(true)
//                .sampler(Sampler.ALWAYS_SAMPLE)
//                .build();
//        return BraveTracer.create(braveTracer);
//    }

}
