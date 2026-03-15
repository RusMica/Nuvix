package com.covielloDevs.SistemaDeVerificacion.controllers;

import com.covielloDevs.SistemaDeVerificacion.models.participante.dto.DtoMovimiento;
import com.covielloDevs.SistemaDeVerificacion.services.MovimientoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/movimientos")
public class MovimientoController {

    private final MovimientoService movimientoService;

    public MovimientoController(MovimientoService movimientoService) {
        this.movimientoService = movimientoService;
    }

    @GetMapping("/participante/{id}")
    public ResponseEntity<Page<DtoMovimiento>> getMovimientosByParticipante(@PathVariable Long id, Pageable pageable){
        return ResponseEntity.ok(movimientoService.getMovimientosByParticipanteId(id,pageable));
    }
}
