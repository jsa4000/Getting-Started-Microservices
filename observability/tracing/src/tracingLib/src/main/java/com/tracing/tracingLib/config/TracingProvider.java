package com.tracing.tracingLib.config;

public enum TracingProvider {
    JAEGER("JAEGER"), ZIPKIN("ZIPKIN");

    private final String name;

    TracingProvider(final String name) { this.name = name; }

    @Override
    public String toString() { return name; }
}
