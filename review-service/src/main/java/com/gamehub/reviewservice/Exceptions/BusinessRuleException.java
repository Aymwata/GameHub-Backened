package com.gamehub.reviewservice.Exceptions;

public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {

        super(message);
    }
}