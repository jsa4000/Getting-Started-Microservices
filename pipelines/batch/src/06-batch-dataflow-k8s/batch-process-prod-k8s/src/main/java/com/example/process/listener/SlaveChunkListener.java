package com.example.process.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Slf4j
@Component
@Profile("worker")
public class SlaveChunkListener implements ChunkListener {

    @Override
    public void beforeChunk(ChunkContext context) {
        log.info("Processing beforeChunk");

        log.info("Job Parameters: " + context.getStepContext()
                .getJobParameters().entrySet().stream()
                .map(x -> x.getKey() + " : " + x.getValue())
                .collect(Collectors.joining( "," )));

        log.info("Step Parameters: " + context.getStepContext()
                .getStepExecutionContext().entrySet().stream()
                .map(x -> x.getKey() + " : " + x.getValue())
                .collect(Collectors.joining( "," )));
    }

    @Override
    public void afterChunk(ChunkContext context) {
        log.info("Processing afterChunk");
    }

    @Override
    public void afterChunkError(ChunkContext context) {
        log.info("Processing afterChunkError");
    }
}
