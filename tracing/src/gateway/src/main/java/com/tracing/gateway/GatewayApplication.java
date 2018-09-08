package com.tracing.gateway;

import io.jaegertracing.Configuration;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GatewayApplication {

	public static void main(String[] args) {

		GlobalTracer.register( new io.jaegertracing.Configuration("gateway-service" )
				.withSampler(new Configuration.SamplerConfiguration().withType("const").withParam(1))
				.withReporter(new Configuration.ReporterConfiguration()
						.withSender(new Configuration.SenderConfiguration().withAgentHost("10.0.0.10").withAgentPort(5775)))
				.getTracer());

		SpringApplication.run(GatewayApplication.class, args);
	}
}
