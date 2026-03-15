package com.covielloDevs.SistemaDeVerificacion.models.usuario.dto;


import com.covielloDevs.SistemaDeVerificacion.models.usuario.Usuario;
import com.covielloDevs.SistemaDeVerificacion.utils.enums.Rol;

public record DtoDatosUsuario(
        String email,
        Rol rol,
        String estado
) {
    public DtoDatosUsuario(Usuario usuario) {
        this(usuario.getEmail(),
                usuario.getRol(),
                usuario.getActivo() ? "Activo" : "Inactivo");
    }
}
