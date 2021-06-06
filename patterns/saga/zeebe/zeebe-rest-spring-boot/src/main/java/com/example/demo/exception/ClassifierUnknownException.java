package com.example.demo.exception;

public class ClassifierUnknownException extends Exception {

    public ClassifierUnknownException() { super(); }

    public ClassifierUnknownException(String message) { super(message); }

    public ClassifierUnknownException(String message, Throwable cause) { super(message, cause); }

    public ClassifierUnknownException(Throwable cause) { super(cause); }

    public ClassifierUnknownException(String message, Throwable cause, boolean enableSuppression,
                                boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
