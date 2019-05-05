package com.example.orchestrator;

import com.example.orchestrator.configuration.DataFlowTestConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes={DataFlowTestConfiguration.class})
@SpringBootTest
public class WebApplicationTests {

    @Test
    public void contextLoads() {
    }

}
