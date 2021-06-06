package com.example.demo.exception;

public class ClassifierNotFoundException extends Exception {

    public ClassifierNotFoundException() { super(); }

    public ClassifierNotFoundException(String message) { super(message); }

    public ClassifierNotFoundException(String message, Throwable cause) { super(message, cause); }

    public ClassifierNotFoundException(Throwable cause) { super(cause); }

    public ClassifierNotFoundException(String message, Throwable cause, boolean enableSuppression,
                                       boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
