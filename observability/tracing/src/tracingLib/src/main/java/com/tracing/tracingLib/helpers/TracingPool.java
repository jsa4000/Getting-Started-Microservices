package com.tracing.tracingLib.helpers;

import io.opentracing.Span;
import io.opentracing.contrib.concurrent.TracedExecutorService;
import io.opentracing.util.GlobalTracer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TracingPool {

    public static ExecutorService createPool(Integer poolSize) {
        return new TracedExecutorService(Executors.newFixedThreadPool(poolSize), GlobalTracer.get());
    }

    public static void executeSpan(String name, Runnable action)
    {
        Span createSpan = GlobalTracer.get()
                .buildSpan(name)
                .asChildOf(GlobalTracer.get().activeSpan())
                .start();
        action.run();
        createSpan.finish();
    }

}
