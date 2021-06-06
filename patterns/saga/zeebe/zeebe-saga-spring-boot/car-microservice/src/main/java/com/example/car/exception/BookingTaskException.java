package com.example.car.exception;

public class BookingTaskException extends Exception {

    public BookingTaskException() { super(); }

    public BookingTaskException(String message) { super(message); }

    public BookingTaskException(String message, Throwable cause) { super(message, cause); }

    public BookingTaskException(Throwable cause) { super(cause); }

    public BookingTaskException(String message, Throwable cause, boolean enableSuppression,
                                boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
