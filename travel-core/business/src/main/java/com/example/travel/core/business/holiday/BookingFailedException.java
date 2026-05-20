package com.example.travel.core.business.holiday;

public class BookingFailedException extends RuntimeException {
    public BookingFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}