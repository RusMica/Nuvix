package com.covielloDevs.SistemaDeVerificacion.models.dto;

public record DtoCreatePreference(
        String id,
        String title,
        String description,
        int quantity,
        String unitPriceForSettings,
        String currencyId
) {
}
