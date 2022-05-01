package com.absence.auth.exceptions;

public class BadCredentialException extends Exception {
    public BadCredentialException() {}
    public BadCredentialException(String message) {
        super(message);
    }
}
