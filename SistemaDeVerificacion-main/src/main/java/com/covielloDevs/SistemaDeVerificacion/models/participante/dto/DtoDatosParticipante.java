package com.covielloDevs.SistemaDeVerificacion.models.participante.dto;

import com.covielloDevs.SistemaDeVerificacion.models.participante.Participante;

public record DtoDatosParticipante(
        String apellido,
        String nombre,
        String dni,
        String email
) {
    public DtoDatosParticipante(Participante participante) {
        this(participante.getApellido(), participante.getNombre(), participante.getDni(), participante.getEmail());
    }

}
