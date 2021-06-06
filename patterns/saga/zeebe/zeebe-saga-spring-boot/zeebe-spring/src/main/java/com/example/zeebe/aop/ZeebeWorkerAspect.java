package com.example.zeebe.aop;

import io.zeebe.client.api.response.ActivatedJob;
import io.zeebe.client.api.worker.JobClient;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.util.Arrays;

@Slf4j
@Aspect
public class ZeebeWorkerAspect {

    @Around("matchZeebeWorker()")
    public void processZeebeWorker(ProceedingJoinPoint joinPoint){
        try {
            joinPoint.proceed();
        } catch (Throwable ex) {
            log.error("Zeebe Worker Exception", ex);
            JobClient client = (JobClient)getParameterByName(joinPoint, "client");
            ActivatedJob job = (ActivatedJob)getParameterByName(joinPoint, "job");
            if (job.getRetries() > 1) {
                log.info("The job {} is retrying. Total retries left = {}", job.getKey(), job.getRetries() - 1);
                client.newFailCommand(job.getKey()).retries(job.getRetries() - 1).send().join();
            }
            else {
                client.newThrowErrorCommand(job.getKey())
                        .errorCode(ex.getMessage())
                        .errorMessage(ex.getMessage())
                        .send()
                        .join();
            }
        }
    }

    @Pointcut("@annotation(io.zeebe.spring.client.annotation.ZeebeWorker)")
    private void matchZeebeWorker() {}

    private Object getParameterByName(ProceedingJoinPoint proceedingJoinPoint, String parameterName) {
        MethodSignature methodSig = (MethodSignature) proceedingJoinPoint.getSignature();
        Object[] args = proceedingJoinPoint.getArgs();
        int idx = Arrays.asList(methodSig.getParameterNames()).indexOf(parameterName);
        if(args.length > idx)
            return args[idx];
        return null;
    }
}

