package com.bridge.herofincorp.exceptions;

public class DealerNotFoundException extends RuntimeException{
    public DealerNotFoundException(String message){
        super(message);
    }
}
