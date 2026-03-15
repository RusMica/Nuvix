package com.covielloDevs.SistemaDeVerificacion.utils.exceptions.email;

public class EmailBadRequestException extends RuntimeException {
    public EmailBadRequestException(String message) {
        super(message);
    }
}
