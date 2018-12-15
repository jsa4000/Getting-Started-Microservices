package com.example.management.controller;

import org.springframework.hateoas.VndErrors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Optional;

//@ControllerAdvice
@RequestMapping(produces = "application/vnd.error+json")
public class ExceptionController extends ResponseEntityExceptionHandler {

    private ResponseEntity<VndErrors> error( Exception exception, HttpStatus httpStatus, String logRef) {
        final String message = Optional.of(exception.getMessage())
                .orElse(exception.getClass().getSimpleName());
        return new ResponseEntity<>(new VndErrors(logRef, message), httpStatus);
    }

    @ExceptionHandler(value = { RuntimeException.class })
    protected ResponseEntity<VndErrors> handleException( RuntimeException ex, WebRequest request) {
        return error(ex, HttpStatus.INTERNAL_SERVER_ERROR,request.toString());
    }

}
