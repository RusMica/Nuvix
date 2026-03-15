package com.covielloDevs.SistemaDeVerificacion.models.usuario.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record DtoEmailRequest(@Email @NotBlank String email) {
}
