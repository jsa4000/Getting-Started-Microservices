package com.example.process.batch;

import io.minio.MinioClient;
import io.minio.Result;
import io.minio.errors.*;
import io.minio.messages.DeleteError;
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
import org.springframework.util.FileSystemUtils;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@Profile("master")
public class PostProcessTasklet implements Tasklet {

    @Autowired
    MinioClient client;

    @Autowired
    ResourcePatternResolver resourcePatternResolver;

    @Value("${batch.tempPath:/tmp/data}")
    String tempPath;

    @Value("${batch.filePattern:*.csv.zip}")
    String filePattern;

    @Value("${batch.resourcesPath:dataflow-bucket}")
    String resourcesPath;

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws IOException, InvalidKeyException, NoSuchAlgorithmException, InsufficientDataException, InternalException, NoResponseException, InvalidBucketNameException, XmlPullParserException, ErrorResponseException {
        log.info("Started Post-Processing");

        Resource[] resources;
        try {
            resources = resourcePatternResolver.getResources("file:" + tempPath + "/" + filePattern);
            log.info("Current files to delete are: " + resources.length);
        } catch (IOException e) {
            throw new RuntimeException("I/O problems when resolving" + " the input file pattern.", e);
        }

        String[] parts = resourcesPath.split("/");
        final String bucketName = parts[0];
        final String objectPath = parts.length > 1 ? parts[1] + "/" : "";

        List<String> objectNames = Arrays.stream(resources)
                .map(x -> objectPath + x.getFilename())
                .collect(Collectors.toList());

        log.info("Objects buckets to remove are: " + objectNames.size());
        for (Result<DeleteError> errorResult: client.removeObjects(bucketName, objectNames)) {
            DeleteError error = errorResult.get();
            log.error("Failed to remove '" + error.objectName() + "'. Error:" + error.message());
        }

        log.info("Deleting all the files");
        FileSystemUtils.deleteRecursively(new File(tempPath));

        log.info("Finished Post-Processing");
        return RepeatStatus.FINISHED;
    }
}