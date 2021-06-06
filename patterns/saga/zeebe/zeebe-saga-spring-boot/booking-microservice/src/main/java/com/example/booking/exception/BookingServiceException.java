package com.example.booking.exception;

public class BookingServiceException extends Exception {

    public BookingServiceException() { super(); }

    public BookingServiceException(String message) { super(message); }

    public BookingServiceException(String message, Throwable cause) { super(message, cause); }

    public BookingServiceException(Throwable cause) { super(cause); }

    public BookingServiceException(String message, Throwable cause, boolean enableSuppression,
                                   boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
