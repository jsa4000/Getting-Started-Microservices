package com.example.batch.batch;

import com.example.batch.utils.Zip;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class PreProcessTasklet implements Tasklet {

    @Value("${batch.inputFile:sample-data.zip}")
    String filename;

    @Value("${batch.resourcesPath:src/main/resources/data}")
    String resourcesPath;

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws IOException {
        log.info("Started Pre-Processing");

        log.info("Uncompressing Content to Process");
        int files = Zip.unZip(filename, resourcesPath );
        log.info("Files uncompressed " + files);

        log.info("Finished Pre-Processing");
        return RepeatStatus.FINISHED;
    }
}