package com.rrg.urlshortener.exception;

public class MissingFieldException extends RuntimeException {

    public MissingFieldException(String message) {
        super(message);
    }
}
