package com.example.batch.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.FileSystemUtils;

import java.io.File;

@Slf4j
@Component
public class PostProcessTasklet implements Tasklet {

    @Value("${batch.tempDir:src/main/resources/data}")
    String tempDir;

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) {
        log.info("Started Post-Processing");

        log.info("Deleting all the files");
        FileSystemUtils.deleteRecursively(new File(tempDir));

        log.info("Finished Post-Processing");
        return RepeatStatus.FINISHED;
    }
}