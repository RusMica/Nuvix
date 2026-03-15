package com.covielloDevs.SistemaDeVerificacion.models.usuario.dto;
import com.covielloDevs.SistemaDeVerificacion.utils.enums.Rol;
import com.covielloDevs.SistemaDeVerificacion.utils.enums.Sexo;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record DtoUpdateUsuarioAdmin(
        String apellido,
        String nombre,
        @Size(min = 7, max = 8, message = "El DNI debe tener entre 7 y 8 caracteres")
        String dni,
        @Email(message = "El email no es valido")
        String email,
        String telefono,
        Rol rol
) {
}
