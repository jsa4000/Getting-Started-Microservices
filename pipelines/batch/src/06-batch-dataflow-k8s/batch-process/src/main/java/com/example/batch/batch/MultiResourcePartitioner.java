package com.example.batch.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@Profile("master")
public class MultiResourcePartitioner implements Partitioner {

    private static final String FILENAME_KEY = "fileName";
    private static final String SOURCE_KEY = "sourceKey";
    private static final String PARTITION_KEY = "partition";

    @Autowired
    ResourcePatternResolver resourcePatternResolver;

    @Value("${batch.inputFile:dataflow-bucket:sample-data.zip}")
    String filename;

    @Value("${batch.tempPath:/tmp/data}")
    String tempPath;

    @Value("${batch.resourcesPath:dataflow-bucket}")
    String resourcesPath;

    @Value("${batch.filePattern:*.csv}")
    String filePattern;

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        log.info("Creating the partitions. gridSize=" + gridSize);

        Resource[] resources;
        try {
            resources = resourcePatternResolver.getResources("file:" + tempPath + "/" + filePattern);
            log.info("Current files to partition are: " + resources.length);
        } catch (IOException e) {
            throw new RuntimeException("I/O problems when resolving" + " the input file pattern.", e);
        }

        Map<String, ExecutionContext> map = new HashMap<>(gridSize);
        int i = 0;
        for (Resource resource : resources) {
            ExecutionContext context = new ExecutionContext();
            log.info("Adding " + resourcesPath + ":" + resource.getFilename() + " file to partition");
            context.putString(FILENAME_KEY, resourcesPath + ":" + resource.getFilename());
            context.putString(SOURCE_KEY, filename);
            map.put(PARTITION_KEY + i, context);
            i++;
        }
        return map;
    }
}
