package com.covielloDevs.SistemaDeVerificacion.controllers;

import com.covielloDevs.SistemaDeVerificacion.models.entrada.dto.DtoDatosEntrada;
import com.covielloDevs.SistemaDeVerificacion.services.EntradaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/entradas")
public class EntradaController {

    private final EntradaService entradaService;

    public EntradaController(EntradaService entradaService) {
        this.entradaService = entradaService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<DtoDatosEntrada> getEntrada(@PathVariable Long id) {
        return ResponseEntity.ok(entradaService.getEntrada(id));
    }

    @GetMapping("/use/entrada/{token}")
    public ResponseEntity<DtoDatosEntrada> validarEntrada(@PathVariable String token) {
        return ResponseEntity.ok(entradaService.validarEntrada(token));
    }

    @GetMapping("/use/salida/{token}")
    public ResponseEntity<DtoDatosEntrada> validarSalida(@PathVariable String token) {
        return ResponseEntity.ok(entradaService.validarSalida(token));
    }
}
