package com.covielloDevs.SistemaDeVerificacion.models.participante.dto;

import com.covielloDevs.SistemaDeVerificacion.utils.enums.TipoIngreso;

import java.time.LocalDateTime;

public record DtoMovimiento(
        LocalDateTime movimiento,
        TipoIngreso tipoIngreso
) {
}
