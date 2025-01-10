package com.onesquad.formulafan.application.exception;

public class FieldValueAlreadyUsedException extends RuntimeException {
    public FieldValueAlreadyUsedException(String message) {
        super(message);
    }
}
