package com.example.process.listener;

import com.example.process.batch.MultiResourcePartitioner;
import com.example.process.utils.Zip;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.stream.Collectors;

@Slf4j
@Component
@Profile("worker")
public class SlaveStepListener implements StepExecutionListener {

    @Value("${batch.tempPath:/tmp/data}")
    String tempPath;

    @Autowired
    MinioClient client;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        log.info("Processing beforeStep");

        log.info("Step Parameters: " + stepExecution.getExecutionContext()
                .entrySet().stream()
                .map(x -> x.getKey() + " : " + x.getValue())
                .collect(Collectors.joining( "," )));

        final String source = String.valueOf(stepExecution
                .getExecutionContext()
                .get(MultiResourcePartitioner.RESOURCE_KEY));

        try {

            log.info("Slave processing the file: " + source );
            String[] parts = source.split(":");
            String bucketName = parts[0];
            String objectName = parts[1];

            log.debug("Bucket Name: " + bucketName);
            log.debug("Object Name: " + objectName);

            client.statObject(bucketName, objectName);

            String slaveTempPath = tempPath + "/slave_data";
            File destDir = new File(slaveTempPath);

            log.info("Creating the temp directory.");

            if (!destDir.exists() && !destDir.mkdirs()) {
                throw new Exception("Folder cannot be created: " + slaveTempPath);
            }

            log.info("Fetching the file to process locally.");

            InputStream inStream = client.getObject(bucketName, objectName);

            final String filename = parts[1].split("/")[parts[1].split("/").length - 1];
            File targetFile = new File(slaveTempPath + "/" + filename);
            OutputStream outStream = new FileOutputStream(targetFile);

            byte[] buffer = new byte[16 * 1024];
            int bytesRead;
            while ((bytesRead = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, bytesRead);
            }
            inStream.close();
            outStream.close();

            log.info("Uncompressing Content to Process");

            int files = Zip.unZip(targetFile.getAbsolutePath(), targetFile.getParentFile().getAbsolutePath() );
            log.info("Files uncompressed " + files);
            log.debug("File Absolute Path: " + targetFile.getAbsolutePath());
            log.debug("File Parent Path: " + targetFile.getParentFile().getAbsolutePath());

            stepExecution.getExecutionContext().put("resourceFile",
                    "file:"  + targetFile.getAbsolutePath().replace(".zip",""));

        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        log.info("Processing afterStep");
        log.info("Exit Status: " +  stepExecution.getExitStatus().getExitDescription());
        return stepExecution.getExitStatus();
    }
}
