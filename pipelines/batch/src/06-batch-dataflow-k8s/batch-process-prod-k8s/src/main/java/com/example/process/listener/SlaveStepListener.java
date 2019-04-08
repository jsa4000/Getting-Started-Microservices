package com.example.process.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

import java.util.stream.Collectors;

@Slf4j
public class SlaveStepListener implements StepExecutionListener {

    @Override
    public void beforeStep(StepExecution stepExecution) {
        log.info("Processing beforeStep");

        log.info("Step Parameters: " + stepExecution.getExecutionContext()
                .entrySet().stream()
                .map(x -> x.getKey() + " : " + x.getValue())
                .collect(Collectors.joining( "," )));
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        log.info("Processing afterStep");
        log.info("Exit Status: " +  stepExecution.getExitStatus().getExitDescription());
        return stepExecution.getExitStatus();
    }
}
