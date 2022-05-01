package com.absence.auth.exceptions;

public class InputValidationException extends Exception {
    public InputValidationException() {}
    public InputValidationException(String message) {
        super(message);
    }
}
