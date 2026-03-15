package com.covielloDevs.SistemaDeVerificacion.controllers;

import com.covielloDevs.SistemaDeVerificacion.models.dto.DtoCrearPreferenciaPago;
import com.covielloDevs.SistemaDeVerificacion.services.pagos.PagoMercadopagoService;
import com.covielloDevs.SistemaDeVerificacion.services.pagos.WebHookService;
import com.mercadopago.exceptions.MPApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/v1/payment")
public class MercadopagoController {

    private static final Logger log = LoggerFactory.getLogger(MercadopagoController.class);

    private final PagoMercadopagoService pagoMercadopagoService;
    private final WebHookService webHookService;
    public MercadopagoController(PagoMercadopagoService pagoMercadopagoService, WebHookService webHookService) {
        this.pagoMercadopagoService = pagoMercadopagoService;
        this.webHookService = webHookService;
    }

    @PostMapping("/buy-license")
    public ResponseEntity<Map<String, String>> createPayment(@RequestBody DtoCrearPreferenciaPago datosPreferencia) {
        try {
            return ResponseEntity.ok(pagoMercadopagoService.crearPreferenciaDePago(datosPreferencia));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al crear la preferencia de pago: " + e.getMessage()));
        }
    }

    @PostMapping(path = "/notifications", consumes = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public ResponseEntity<Void> handleWebhookNotification(@RequestParam Map<String, String> notification) {
        log.info(">>> Notificación de MercadoPago recibida: {}", notification);
        try {
            webHookService.handleWebHookNotification(notification);
            return ResponseEntity.ok().build();
        } catch (MPApiException e) {
            log.error("Error de API de MercadoPago al procesar webhook: {} - Response: {}",
                    e.getMessage(), e.getApiResponse().getContent());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            log.error("Error inesperado al procesar webhook: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}