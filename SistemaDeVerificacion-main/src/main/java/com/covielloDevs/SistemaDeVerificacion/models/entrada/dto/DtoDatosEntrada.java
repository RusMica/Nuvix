package com.covielloDevs.SistemaDeVerificacion.models.entrada.dto;

import com.covielloDevs.SistemaDeVerificacion.models.entrada.Entrada;
import com.covielloDevs.SistemaDeVerificacion.models.participante.dto.DtoDatosParticipante;
import com.covielloDevs.SistemaDeVerificacion.utils.enums.EstadoEntrada;

import java.util.UUID;

public record DtoDatosEntrada(
        Long id,
        UUID token,
        EstadoEntrada estado,
        DtoDatosParticipante participante
) {
    public DtoDatosEntrada(Entrada entrada) {
        this(entrada.getId(), entrada.getToken(), entrada.getEstado(), new DtoDatosParticipante(entrada.getParticipante()));
    }
}
