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
        docker run -d -p 5775:5775/udp -p 16686:16686 jaegertracing/all-in-one:latest

        # Using Zipkin instead and use gradle dependencies.
        docker run --rm -it -p 9411:9411 openzipkin/zipkin

    > Use the [Jaegger client documentation](https://github.com/jaegertracing/jaeger-client-java/tree/master/jaeger-core) and [Available protocols](https://www.jaegertracing.io/docs/1.6/getting-started/) to configure the jaeger's address and port.

1. Gradle dependencies (opentracing and jaeger)

    ```Groovy
    // https://mvnrepository.com/artifact/io.opentracing/opentracing-api
    compile (group: 'io.opentracing', name: 'opentracing-api', version: '0.31.0')
    // https://search.maven.org/artifact/io.jaegertracing/jaeger-client
    compile ('io.jaegertracing:jaeger-client:0.31.0')
    // https://mvnrepository.com/artifact/io.zipkin.reporter2/zipkin-reporter
    compile (group: 'io.zipkin.reporter2', name: 'zipkin-reporter', version: '2.7.7')
    ```

2. Basically use the following snippet to use OpenTracing API and Jaeger implementation

    ```java
    import com.uber.jaeger.Configuration;
    import io.opentracing.Span;
    import io.opentracing.util.GlobalTracer;

    ...

    GlobalTracer.register(
        new Configuration(
            "your_service_name",
            new Configuration.SamplerConfiguration("const", 1),
            new Configuration.ReporterConfiguration(
                false, "localhost", null, 1000, 10000)
        ).getTracer());

    ...

    try (Span parent = GlobalTracer.get()
                .buildSpan("hello")
                .start()) {
        try (Span child = GlobalTracer.get()
                .buildSpan("world")
                .asChildOf(parent)
                .start()) {
        }
    }
    ```

## References

[Quiuck Start](http://opentracing.io/documentation/pages/quick-start)
[Open Tracing Web](http://opentracing.io/)
[OpenTracing API](https://github.com/opentracing)
[OpenTracing API for JAVA](https://github.com/opentracing/opentracing-java)
[Jaeger Client For Java](https://github.com/jaegertracing/jaeger-client-java)
