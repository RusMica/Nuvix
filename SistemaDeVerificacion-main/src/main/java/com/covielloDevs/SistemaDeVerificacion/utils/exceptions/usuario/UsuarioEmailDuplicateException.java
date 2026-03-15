package com.covielloDevs.SistemaDeVerificacion.utils.exceptions.usuario;

public class UsuarioEmailDuplicateException extends RuntimeException {
    public UsuarioEmailDuplicateException(String message) {
        super(message);
    }
}
