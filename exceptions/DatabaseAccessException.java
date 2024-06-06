package com.bridge.herofincorp.exceptions;

public class DatabaseAccessException extends RuntimeException{
    public DatabaseAccessException(String message) {
        super(message);
    }
}
