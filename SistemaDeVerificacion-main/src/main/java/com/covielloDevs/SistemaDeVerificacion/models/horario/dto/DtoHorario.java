package com.covielloDevs.SistemaDeVerificacion.models.horario.dto;


import com.covielloDevs.SistemaDeVerificacion.models.horario.Horario;

import java.time.format.DateTimeFormatter;

public record DtoHorario(String dia, String hora) {

    public DtoHorario(Horario horario){
        this(horario.getDia().name(), horario.getHora()
                .format(DateTimeFormatter.ofPattern("HH:mm")));
    }
}
