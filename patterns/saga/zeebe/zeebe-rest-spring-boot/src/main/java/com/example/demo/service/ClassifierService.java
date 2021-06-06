package com.example.demo.service;

import com.example.demo.domain.DataType;
import reactor.core.publisher.Mono;

public interface ClassifierService {

    /**
     * Method to classify a data type by its reason
     *
     * @param reason
     * @return
     */
    Mono<DataType> classifySync(String reason);

    /**
     * Method to classify a data type by its reason
     *
     * @param reason
     * @return
     */
    Mono<DataType> classifyAsync(String reason);

}
