package com.covielloDevs.SistemaDeVerificacion.models.evento.dto;

import com.covielloDevs.SistemaDeVerificacion.models.evento.Evento;
import com.covielloDevs.SistemaDeVerificacion.models.participante.Participante;
import com.covielloDevs.SistemaDeVerificacion.models.participante.dto.DtoDatosParticipante;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public record DtoDatosEvento(
        Long id,
        String nombre,
        LocalDate fecha,
        String listaParticipantes,
        List<DtoDatosParticipante> participantes
) {
    public DtoDatosEvento(Evento evento) {
        this(evento.getId(), evento.getNombre(), evento.getFecha(), evento.getListaParticipantes(),
                evento.getParticipantes().stream().map(DtoDatosParticipante::new).toList());
    }

}
