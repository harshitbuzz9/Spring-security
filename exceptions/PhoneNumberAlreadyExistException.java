package com.bridge.herofincorp.exceptions;

public class PhoneNumberAlreadyExistException extends RuntimeException{
    public PhoneNumberAlreadyExistException(String message) {
        super(message);
    }
}
