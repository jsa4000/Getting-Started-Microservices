package com.example.process.listeners;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Slf4j
@Component
@Profile("master")
public class JobCompletionListener extends JobExecutionListenerSupport {

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("Processing beforeJob");

        log.info("Job Parameters: " + jobExecution.getJobParameters()
                .getParameters()
                .entrySet().stream()
                .map(x -> x.getKey() + " : " + x.getValue())
                .collect(Collectors.joining( "," )));

        log.info("Execution Context Parameters: " + jobExecution.getExecutionContext()
                .entrySet().stream()
                .map(x -> x.getKey() + " : " + x.getValue())
                .collect(Collectors.joining( "," )));
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("!!! JOB FINISHED! Time to verify the results");
        }
    }
}
