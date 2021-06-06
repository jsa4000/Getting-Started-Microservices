package com.example.demo.controller;

import com.example.demo.domain.DataType;
import com.example.demo.exception.ClassifierNotFoundException;
import com.example.demo.exception.ClassifierWorkflowException;
import com.example.demo.service.ClassifierService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class ClassifierController {

    private final ClassifierService classifierService;

    @GetMapping("/classify/{reason}")
    Mono<ResponseEntity<DataType>> classify(@PathVariable("reason") String reason) {
        return classifierService.classifySync(reason)
                .map(ResponseEntity::ok)
                .onErrorMap(this::handleError);
    }

    private Throwable handleError(Throwable ex) {
        if (ex instanceof ClassifierNotFoundException)
            return new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        return new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }
}
