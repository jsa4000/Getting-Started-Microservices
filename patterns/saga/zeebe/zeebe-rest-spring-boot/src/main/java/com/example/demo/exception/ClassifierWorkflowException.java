package com.example.demo.exception;

public class ClassifierWorkflowException extends Exception {

    public ClassifierWorkflowException() { super(); }

    public ClassifierWorkflowException(String message) { super(message); }

    public ClassifierWorkflowException(String message, Throwable cause) { super(message, cause); }

    public ClassifierWorkflowException(Throwable cause) { super(cause); }

    public ClassifierWorkflowException(String message, Throwable cause, boolean enableSuppression,
                                       boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
