package com.example.process.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@Profile("master")
public class MultiResourcePartitioner implements Partitioner {

    private static final String FILENAME_KEY = "fileName";
    private static final String PARTITION_KEY = "partition";

    @Value("${batch.resourcesPath:dataflow-bucket}")
    String resourcesPath;

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        log.info("Creating the partitions. gridSize=" + gridSize);

        String[] resources = {
                "first_resource",
                "second_resource",
                "third_resource",
                "fourth_resource",
                "fifth_resource",
                "sixth_resource",
        };

        Map<String, ExecutionContext> map = new HashMap<>(gridSize);
        int i = 0;
        for (String resource : resources) {
            ExecutionContext context = new ExecutionContext();
            log.info("Adding " + resourcesPath + ":" + resource + " resource to partition");
            context.putString(FILENAME_KEY, resourcesPath + ":" + resource);
            map.put(PARTITION_KEY + i, context);
            i++;
        }
        return map;
    }
}
