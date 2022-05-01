package com.absence.auth.exceptions;

public class ResourceNotFoundException extends Exception {
    public ResourceNotFoundException() {}
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
