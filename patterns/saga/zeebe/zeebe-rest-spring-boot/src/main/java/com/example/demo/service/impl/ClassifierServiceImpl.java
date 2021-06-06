package com.example.demo.service.impl;

import com.example.demo.domain.DataType;
import com.example.demo.exception.ClassifierNotFoundException;
import com.example.demo.exception.ClassifierUnknownException;
import com.example.demo.exception.ClassifierWorkflowException;
import com.example.demo.service.ClassifierService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.zeebe.client.ZeebeClient;
import io.zeebe.client.api.command.ClientStatusException;
import io.zeebe.client.api.response.WorkflowInstanceResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClassifierServiceImpl implements ClassifierService {

    private final ZeebeClient zeebeClient;

    public final ObjectMapper objectMapper;

    @Override
    public Mono<DataType> classifySync(String reason) {
        try {
            WorkflowInstanceResult result = zeebeClient.newCreateInstanceCommand()
                    .bpmnProcessId("emergency-process")
                    .latestVersion()
                    .variables(Map.of("emergencyReason", reason))
                    .withResult()
                    // This filters only the variables to be retrieved from the Job
                    //  .fetchVariables(List.of("emergencyType"))
                    // It can be set a timeout for the response so it will be captured by this method
                    .requestTimeout(zeebeClient.getConfiguration().getDefaultJobTimeout())
                    .send()
                    .join();

            log.info(result.getVariablesAsMap().toString());

            // ZeebeObjectMapper cannot be override yet: https://github.com/zeebe-io/spring-zeebe/issues/109
            //return Mono.just(result.getVariablesAsType(DataType.class));

            if (!result.getVariablesAsMap().containsKey("response"))
                throw new ClassifierNotFoundException();
            return Mono.just(objectMapper.readValue(result.getVariablesAsMap().get("response").toString(), DataType.class));
        } catch (ClassifierNotFoundException ex) {
            return Mono.error(ex);
        } catch (ClientStatusException ex) {
            return Mono.error(new ClassifierWorkflowException(ex));
        } catch (Exception ex) {
            return Mono.error(new ClassifierUnknownException(ex));
        }
    }

    @Override
    public Mono<DataType> classifyAsync(String reason) {
        zeebeClient.newCreateInstanceCommand()
                .bpmnProcessId("emergency-process")
                .latestVersion()
                .variables(Map.of("emergencyReason", reason))
                .send()
                .join();

        return Mono.just(DataType.builder()
                .build());
    }
}
