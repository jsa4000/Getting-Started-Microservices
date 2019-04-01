package com.example.generator.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class TimingAspect {

    @Around("@annotation(com.example.generator.aop.LogExecutionTime)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.nanoTime();
        Object result = joinPoint.proceed();
        log.info(joinPoint.getSignature().toShortString() + " executed in " +
                ((System.nanoTime() - start) / 1000000) + "ms");
        return result;
    }

}