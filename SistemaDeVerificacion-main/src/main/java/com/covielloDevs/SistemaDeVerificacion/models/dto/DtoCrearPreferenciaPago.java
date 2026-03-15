package com.covielloDevs.SistemaDeVerificacion.models.dto;

import com.covielloDevs.SistemaDeVerificacion.utils.enums.TipoPreferencia;
import jakarta.validation.constraints.NotNull;

public record DtoCrearPreferenciaPago(
        @NotNull
        TipoPreferencia tipoPreferencia,
        Long usuarioId, // Opcional, para asociar el pago
        Integer cantidad // Opcional, para compras como licencias
) {}