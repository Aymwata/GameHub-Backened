package com.gamehub.reviewservice.Exceptions;

public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        // Pasamos el mensaje a la clase padre (RuntimeException)
        super(message);
    }
}