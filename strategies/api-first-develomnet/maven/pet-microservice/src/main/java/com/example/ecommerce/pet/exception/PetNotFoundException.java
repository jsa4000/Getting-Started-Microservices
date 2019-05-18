package com.example.ecommerce.pet.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PetNotFoundException extends RuntimeException {

    public PetNotFoundException() { super(); }
    public PetNotFoundException(String message, Throwable cause) {super(message, cause); }
    public PetNotFoundException(String message) { super(message); }
    public PetNotFoundException(Throwable cause) {super(cause); }

}