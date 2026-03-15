package com.covielloDevs.SistemaDeVerificacion.models.planMensual.dto;

public record DtoCreatePlanMensual(
        String nombre,
        short cantidadEventos,
        long cantidadInvitados,
        String precioSettingKey
) {
}
