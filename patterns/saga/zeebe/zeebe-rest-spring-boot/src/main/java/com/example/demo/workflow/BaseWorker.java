package com.example.demo.workflow;

import com.example.demo.domain.DataType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.zeebe.client.api.response.ActivatedJob;
import io.zeebe.client.api.worker.JobClient;
import io.zeebe.spring.client.annotation.ZeebeWorker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Map;

@Component
public interface BaseWorker {

    //@ZeebeRetryable
    @ZeebeWorker(type = "classify")
    void classify(final JobClient client, final ActivatedJob job);

    @ZeebeWorker(type = "classify-rollout")
    void classifyRollout(final JobClient client, final ActivatedJob job) throws JsonProcessingException { }

}
