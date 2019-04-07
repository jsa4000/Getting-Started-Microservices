package com.example.process.batch;

import io.minio.MinioClient;
import io.minio.errors.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.xmlpull.v1.XmlPullParserException;

import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Slf4j
@Component
public class DownloadProcessTasklet implements Tasklet {

    @Autowired
    MinioClient client;

    @Value("${batch.inputFile:dataflow-bucket:sample-data.zip}")
    String filename;

    @Value("${batch.tempPath:/tmp/data}")
    String tempPath;

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext)
            throws IOException, XmlPullParserException, InsufficientDataException, NoSuchAlgorithmException,
            NoResponseException, InternalException, InvalidKeyException, InvalidBucketNameException,
            ErrorResponseException, InvalidArgumentException {
        log.info("Started Download-Processing");

        log.info("Download Content");

        String[] parts = filename.split(":");
        String bucketName = parts[0];
        String objectName = parts[1];

        log.debug("Bucket Name: " + bucketName);
        log.debug("Object Name: " + objectName);

        client.statObject(bucketName, objectName);

        File destDir = new File(tempPath);
        if (!destDir.exists()) {
            destDir.mkdir();
        }

        InputStream inStream = client.getObject(bucketName, objectName);
        File targetFile = new File(tempPath + "/" + objectName);
        OutputStream outStream = new FileOutputStream(targetFile);

        byte[] buffer = new byte[16 * 1024];
        int bytesRead;
        while ((bytesRead = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, bytesRead);
        }

        inStream.close();
        outStream.close();

        log.info("File downloaded.");

        log.info("Finished Download-Processing");
        return RepeatStatus.FINISHED;
    }
}