package com.example.management.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class RoleNotFoundException extends RuntimeException {

    public RoleNotFoundException() { super(); }
    public RoleNotFoundException(String message, Throwable cause) {super(message, cause); }
    public RoleNotFoundException(String message) { super(message); }
    public RoleNotFoundException(Throwable cause) {super(cause); }

}