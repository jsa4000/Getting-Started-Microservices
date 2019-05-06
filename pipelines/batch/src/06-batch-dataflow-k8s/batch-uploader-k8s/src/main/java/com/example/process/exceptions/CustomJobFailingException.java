package com.example.process.exceptions;

public class CustomJobFailingException extends Exception {

    public CustomJobFailingException() {
    }

    public CustomJobFailingException(String message) {
        super(message);
    }

    public CustomJobFailingException(String message, Throwable cause) {
        super(message, cause);
    }

    public CustomJobFailingException(Throwable cause) {
        super(cause);
    }

    public CustomJobFailingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
