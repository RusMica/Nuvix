package com.covielloDevs.SistemaDeVerificacion.models.usuario.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record DtoVerifyCode(
        @Email
        @NotBlank(message = "El email es requerido")
        String email,
        @NotBlank(message = "El código es requerido")
        String code
) {
}
