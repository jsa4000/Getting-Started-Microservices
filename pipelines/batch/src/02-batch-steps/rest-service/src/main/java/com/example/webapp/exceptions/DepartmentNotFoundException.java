package com.example.webapp.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class DepartmentNotFoundException extends RuntimeException {

    public DepartmentNotFoundException() { super(); }
    public DepartmentNotFoundException(String message, Throwable cause) {super(message, cause); }
    public DepartmentNotFoundException(String message) { super(message); }
    public DepartmentNotFoundException(Throwable cause) {super(cause); }

}