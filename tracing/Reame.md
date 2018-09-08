# Tracing in Microservices

## Introduction

### Tracing

Developers and engineering organizations are trading in old, monolithic systems for modern microservice architectures, and they do so for numerous compelling reasons: system components scale independently, dev teams stay small and agile, deployments are continuous and decoupled, and so on.

That said, once a production system contends with real concurrency or splits into many services, crucial (and formerly easy) tasks become difficult: *user-facing latency optimization*, *root-cause analysis of backend errors*, *communication about distinct pieces of a now-distributed system*, etc.

Contemporary **distributed tracing systems** (e.g., **Zipkin**, **Jaeger**, Dapper, HTrace, X-Trace, among others) aim to address these issues, but they do so via application-level instrumentation using incompatible APIs. Developers are uneasy about tightly coupling their polyglot systems to any particular distributed tracing implementation, yet the application-level instrumentation APIs for these many distinct tracing systems have remarkably similar semantics.

### Why OpenTracing

Enter **OpenTracing**: by offering **consistent**, **expressive**, **vendor-neutral APIs** for popular platforms, OpenTracing makes it easy for developers to add (or switch) tracing implementations with an O(1) configuration change. OpenTracing also offers a lingua franca for OSS instrumentation and platform-specific tracing helper libraries. Please refer to the Semantic Specification.

### What is a Trace

At the highest level, a **trace** tells the **story** of a **transaction** or workflow as it propagates through a (potentially distributed) system. In OpenTracing, a trace is a **directed acyclic graph** (DAG) of **spans**: named, timed operations representing a contiguous segment of work in that trace.

## Example

> For this example is going to be used Jaeger, however since we are working on OpenTracing it can be used any implementation that followos the standard OpenTracing API.

1. Start Jaeger using docker

        # Using Jaeger
        sudo docker run -d -p 5775:5775/udp -p 6831:6831/udp -p 6832:6832/udp -p 5778:5778 -p 16686:16686 -p 14268:14268 -p 9411:9411 jaegertracing/all-in-one:latest

        # Using Zipkin instead and use gradle dependencies.
        sudo docker run --rm -it -p 9411:9411 openzipkin/zipkin

    > Use the [Jaegger client documentation](https://github.com/jaegertracing/jaeger-client-java/tree/master/jaeger-core) and [Available protocols](https://www.jaegertracing.io/docs/1.6/getting-started/) to configure the jaeger's address and port.

1. Gradle dependencies (opentracing and jaeger)

    ```Groovy
    // https://mvnrepository.com/artifact/io.opentracing/opentracing-api
    compile (group: 'io.opentracing', name: 'opentracing-api', version: '0.31.0')
    // https://github.com/opentracing-contrib/java-spring-jaeger
    compile('io.opentracing.contrib:opentracing-spring-jaeger-web-starter:0.2.1')
    // https://mvnrepository.com/artifact/io.zipkin.reporter2/zipkin-reporter
    compile (group: 'io.zipkin.reporter2', name: 'zipkin-reporter', version: '2.7.7')
    ```

1. Basically use the following snippet to use OpenTracing API and Jaeger implementation

> If no settings are changed, spans will be reported to the UDP port *6831* of *localhost*. The simplest way to change this behavior is to set the following properties:

    ```java
   @Bean
    public Tracer jaegerTracer() {
        return new io.jaegertracing.Configuration("gateway-service" )
                .withSampler(new SamplerConfiguration().withType(ConstSampler.TYPE).withParam(1))
                .withReporter(new ReporterConfiguration().withLogSpans(true)
                        .withSender(new SenderConfiguration().withAgentHost("10.0.0.10").withAgentPort(6831)))
                .getTracer();
    }
    ```*

1. Verify java logs and jaeger with the **traceId** and **Spans**

```txt
2018-09-08 19:17:14.510  INFO 7112 --- [nio-8080-exec-2] i.j.internal.reporters.LoggingReporter   : Span reported: e18a3bb477929bac:e9fe19d397077ed2:b067b08b660904f7:1 - hello
2018-09-08 19:17:14.510  INFO 7112 --- [nio-8080-exec-1] i.j.internal.reporters.LoggingReporter   : Span reported: e18a3bb477929bac:b067b08b660904f7:e18a3bb477929bac:1 - GET
```

## References

[Quiuck Start](http://opentracing.io/documentation/pages/quick-start)
[Open Tracing Web](http://opentracing.io/)
[OpenTracing API](https://github.com/opentracing)
[OpenTracing API for JAVA](https://github.com/opentracing/opentracing-java)
[Jaeger spring Client For Java](https://github.com/opentracing-contrib/java-spring-jaeger)
