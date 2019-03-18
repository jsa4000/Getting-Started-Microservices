package com.example.batch.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class CustomMultiResourcePartitioner implements Partitioner {

    private static final String DEFAULT_KEY_NAME = "fileName";
    private static final String PARTITION_KEY = "partition";

    private Resource[] resources = new Resource[0];
    private String keyName = DEFAULT_KEY_NAME;

    public void setResources(Resource[] resources) {
        this.resources = resources;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        Map<String, ExecutionContext> map = new HashMap<>(gridSize);
        int i = 0, k = 1;
        for (Resource resource : resources) {
            try {
                ExecutionContext context = new ExecutionContext();
                log.info("Adding " + resource.getFile().getAbsolutePath() + " file to partition");
                context.putString(keyName, "file:" + resource.getFile().getAbsolutePath());
                context.putString("opFileName", "output" + k++ + ".xml");
                map.put(PARTITION_KEY + i, context);
                i++;
            } catch (IOException ex) {
                log.error("Error getting the file ");
            }
        }
        return map;
    }
}
