package com.gamehub.orderservice.exceptions;


public class OrdenException extends RuntimeException {
    public OrdenException(String mensaje) {
        super(mensaje);
    }
}