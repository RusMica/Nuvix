package com.covielloDevs.SistemaDeVerificacion.utils;

import com.covielloDevs.SistemaDeVerificacion.utils.enums.TipoDuracion;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class ExpirationDate {

    public static Instant generate(Long time, TipoDuracion type){
        Instant instant = null;
        switch(type){
            case HORA -> instant = LocalDateTime.now().plusHours(time).toInstant(ZoneOffset.of("-03:00"));

            case MINUTO -> instant = LocalDateTime.now().plusMinutes(time).toInstant(ZoneOffset.of("-03:00"));

            case SEGUNDO -> instant = LocalDateTime.now().plusSeconds(time).toInstant(ZoneOffset.of("-03:00"));

            default -> throw new RuntimeException("Tipo de duración inválida");
        }
        return instant;
    }
}
