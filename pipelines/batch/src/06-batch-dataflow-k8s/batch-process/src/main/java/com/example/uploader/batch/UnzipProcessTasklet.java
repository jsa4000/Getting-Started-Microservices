package com.example.uploader.batch;

import com.example.uploader.utils.Zip;
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
public class UnzipProcessTasklet implements Tasklet {

    @Value("${batch.inputFile:dataflow-bucket:sample-data.zip}")
    String filename;

    @Value("${batch.tempPath:/tmp/data}")
    String tempPath;

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws IOException {
        log.info("Started Pre-Processing");

        String[] parts = filename.split(":");
        String objectName = parts[1].trim();

        log.info("Uncompressing Content to Process");
        int files = Zip.unZip(tempPath + "/" + objectName, tempPath );
        log.info("Files uncompressed " + files);

        log.info("Finished Pre-Processing");
        return RepeatStatus.FINISHED;
    }
}