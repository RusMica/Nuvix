package com.covielloDevs.SistemaDeVerificacion.models.usuario.dto;

import com.covielloDevs.SistemaDeVerificacion.utils.enums.Sexo;

import java.time.LocalDate;

public record DtoDatosUsuarioSocio(
        String apellido,
        String nombre,
        String email,
        String telefono,
        LocalDate fechaNacimiento,
        Sexo sexo,
        String direccion
) {
}
