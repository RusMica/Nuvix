package com.covielloDevs.SistemaDeVerificacion.models.dto;

public record DtoEmail(
        String destinatario,
        String asunto,
        String mensaje,
        byte[] qrCode,
        String qrCodeName,
        byte[] pdf,
        String pdfName
) {
}
