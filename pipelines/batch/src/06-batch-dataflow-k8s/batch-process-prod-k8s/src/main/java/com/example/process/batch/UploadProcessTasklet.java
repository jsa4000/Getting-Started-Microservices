package com.example.process.batch;

import com.example.process.utils.Zip;
import io.minio.MinioClient;
import io.minio.errors.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Slf4j
@Component
@Profile("master")
public class UploadProcessTasklet implements Tasklet {

    @Autowired
    ResourcePatternResolver resourcePatternResolver;

    @Autowired
    MinioClient client;

    @Value("${batch.inputFile:dataflow-bucket:sample-data.zip}")
    String filename;

    @Value("${batch.tempPath:/tmp/data}")
    String tempPath;

    @Value("${batch.resourcesPath:dataflow-bucket}")
    String resourcesPath;

    @Value("${batch.filePattern:*.csv.zip}")
    String filePattern;

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws IOException, XmlPullParserException, NoSuchAlgorithmException, InvalidKeyException, InvalidArgumentException, InternalException, NoResponseException, InvalidBucketNameException, InsufficientDataException, ErrorResponseException {
        log.info("Started Upload-Processing");

        log.info("Uploading Content to Process");
        Resource[] resources;
        try {
            log.info("Searching files using " + tempPath + "/" + filePattern.replace(".zip",""));
            resources = resourcePatternResolver.getResources("file:" + tempPath + "/" + filePattern.replace(".zip",""));
            log.info("Current files to process are: " + resources.length);
        } catch (IOException e) {
            throw new RuntimeException("I/O problems when resolving" + " the input file pattern.", e);
        }

        String[] parts = resourcesPath.split("/");
        final String bucketName = parts[0];
        final String objectPath = parts.length > 1 ? parts[1] + "/" : "";

        for (Resource resource : resources) {
            log.debug("Compressing File Name: " + resource.getFilename());
            long start = System.nanoTime();
            Zip.zip(resource.getFile().getAbsolutePath(),resource.getFile().getAbsolutePath() + ".zip");
            log.debug("Compression has been executed in " + ((System.nanoTime() - start) / 1000000) + "ms");

            log.debug("Uploading File to bucket: " + bucketName);
            log.debug("File Name: " + resource.getFilename());
            log.debug("File Path: " + resource.getFile().getAbsolutePath());
            start = System.nanoTime();
            client.putObject(bucketName, objectPath + resource.getFilename() + ".zip",
                    resource.getFile().getAbsolutePath() + ".zip");
            log.debug("Uploading has been executed in " + ((System.nanoTime() - start) / 1000000) + "ms");
        }

        log.info("Files uploaded: " + resources.length);

        log.info("Finished Upload-Processing");
        return RepeatStatus.FINISHED;
    }
}