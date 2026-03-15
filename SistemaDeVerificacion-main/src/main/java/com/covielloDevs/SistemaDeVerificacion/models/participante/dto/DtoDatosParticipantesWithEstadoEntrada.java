package com.covielloDevs.SistemaDeVerificacion.models.participante.dto;

import com.covielloDevs.SistemaDeVerificacion.models.entrada.Entrada;
import com.covielloDevs.SistemaDeVerificacion.models.participante.Participante;
import com.covielloDevs.SistemaDeVerificacion.utils.enums.EstadoEntrada;

public record DtoDatosParticipantesWithEstadoEntrada(
        Long id,
        String nombreCompleto,
        String email,
        String dni,
        EstadoEntrada estadoEntrada
) {
    public DtoDatosParticipantesWithEstadoEntrada(Participante participante, Entrada entrada){
        this(participante.getId(), String.format("%s %s", participante.getApellido(), participante.getNombre()),participante.getEmail()
                , participante.getDni(), entrada.getEstado());
    }
}
