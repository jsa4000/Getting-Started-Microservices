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

@Slf4j
@Component
@RequiredArgsConstructor
public class ClassifierWorker {

    public final ObjectMapper objectMapper;

    @ZeebeWorker(type = "classify")
    public void classifyEmergency(final JobClient client, final ActivatedJob job) {
        logJob(job);
        if (!job.getVariablesAsMap().containsKey("emergencyReason")) { // default to ambulance if no reason is provided
            client.newCompleteCommand(job.getKey()).variables("{\"emergencyType\": \"Injured\"}").send().join();
        }else if (job.getVariablesAsMap().get("emergencyReason").toString().contains("person")) {
            client.newCompleteCommand(job.getKey()).variables("{\"emergencyType\": \"Injured\"}").send().join();
        } else if (job.getVariablesAsMap().get("emergencyReason").toString().contains("fire")) {
            client.newCompleteCommand(job.getKey()).variables("{\"emergencyType\": \"Fire\"}").send().join();
        } else if (job.getVariablesAsMap().get("emergencyReason").toString().contains("retries")) {
            log.info("Retries:" + job.getRetries());
            // Retry the current task again. Retries tells how many retries must be done to the task.
            // Retries parameter can be configured in the Service BPMN component but is managed by the client
            // Since there are not and retriable conditions, this is managed by the client to decide retries policies.
            if (job.getRetries() > 1) {
                client.newFailCommand(job.getKey()).retries(job.getRetries() - 1).send().join();
            }
            else {
                // Throws an error all retries has been done with no success.
                // This is shown in the dashboard as an Alert
                client.newThrowErrorCommand(job.getKey())
                        .errorCode("123")
                        .errorMessage("Unexpected Error in classifyEmergency")
                        .send()
                        .join();
            }
        } else {
            // Workers always must return completed command to Zeebe to proceed with the Workflow
            client.newCompleteCommand(job.getKey()).variables("{\"emergencyType\": \"none\"}").send().join();
        }
    }

    @ZeebeWorker(type = "hospital")
    public void handleHospitalCoordination(final JobClient client, final ActivatedJob job) throws JsonProcessingException {
        logJob(job);
        DataType dataType = DataType.builder()
                .name("Hospital")
                .type("Injured")
                .description("This is an Hospital to health injured people")
                .createdAt(OffsetDateTime.now())
                .versionNumber(12)
                .enabled(true)
                .build();   
        client.newCompleteCommand(job.getKey())
                // ZeebeObjectMapper cannot be override yet: https://github.com/zeebe-io/spring-zeebe/issues/109
                //.variables(dataType)
                .variables(Map.of("response", objectMapper.writeValueAsString(dataType)))
                .send().join();
    }

    @ZeebeWorker(type = "firefighters")
    public void handleFirefighterCoordination(final JobClient client, final ActivatedJob job) throws JsonProcessingException{
        logJob(job);
        DataType dataType = DataType.builder()
                .name("Fire Department")
                .type("Fire")
                .description("This is a Fire Department to put off fires")
                .createdAt(OffsetDateTime.now())
                .versionNumber(3)
                .enabled(false)
                .build();
        client.newCompleteCommand(job.getKey())
                // ZeebeObjectMapper cannot be override yet: https://github.com/zeebe-io/spring-zeebe/issues/109
                //.variables(dataType)
                .variables(Map.of("response", objectMapper.writeValueAsString(dataType)))
                .send().join();
    }

    private static void logJob(final ActivatedJob job) {
        log.info(
                "complete job\n>>> [type: {}, key: {}, element: {}, workflow instance: {}]\n{deadline; {}]\n[headers: {}]\n[variables: {}]",
                job.getType(),
                job.getKey(),
                job.getElementId(),
                job.getWorkflowInstanceKey(),
                Instant.ofEpochMilli(job.getDeadline()),
                job.getCustomHeaders(),
                job.getVariables());
    }

}
