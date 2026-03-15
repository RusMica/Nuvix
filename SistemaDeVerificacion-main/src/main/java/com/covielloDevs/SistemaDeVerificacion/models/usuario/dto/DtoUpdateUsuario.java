package com.covielloDevs.SistemaDeVerificacion.models.usuario.dto;

import com.covielloDevs.SistemaDeVerificacion.utils.enums.Sexo;
import jakarta.validation.constraints.Email;

import java.time.LocalDate;

public record DtoUpdateUsuario(
        String apellido,
        String nombre,
        @Email
        String email,
        String telefono
) {
}
