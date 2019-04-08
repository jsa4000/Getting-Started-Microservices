package com.example.process.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;

@Slf4j
public class SlaveListener implements ChunkListener {
    @Override
    public void beforeChunk(ChunkContext context) {
        log.info("Processing beforeChunk");
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
