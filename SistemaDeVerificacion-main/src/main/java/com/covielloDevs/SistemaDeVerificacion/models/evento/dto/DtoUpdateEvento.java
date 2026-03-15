package com.covielloDevs.SistemaDeVerificacion.models.evento.dto;


import java.time.LocalDate;
import java.time.LocalTime;

public record DtoUpdateEvento(
        String nombre,
        LocalDate fecha,
        String itinerario
) {
}
