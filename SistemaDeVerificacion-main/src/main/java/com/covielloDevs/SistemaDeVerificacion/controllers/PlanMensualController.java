package com.covielloDevs.SistemaDeVerificacion.controllers;

import com.covielloDevs.SistemaDeVerificacion.models.planMensual.PlanMensual;
import com.covielloDevs.SistemaDeVerificacion.models.planMensual.dto.DtoCreatePlanMensual;
import com.covielloDevs.SistemaDeVerificacion.models.planMensual.dto.DtoUpdatePlanMensual;
import com.covielloDevs.SistemaDeVerificacion.repositories.PlanMensualRepository;
import com.covielloDevs.SistemaDeVerificacion.services.PlanMensualService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/v1/planes-mensuales")
public class PlanMensualController {


    private final PlanMensualService planMensualService;

    public PlanMensualController(PlanMensualService planMensualService) {
        this.planMensualService = planMensualService;
    }

    @GetMapping
    public ResponseEntity<List<PlanMensual>> getAllPlanes() {
        return ResponseEntity.ok(planMensualService.getPlanes());
    }

    @PostMapping
    @Transactional
    public ResponseEntity<PlanMensual> createPlan(@RequestBody DtoCreatePlanMensual dto) {
        PlanMensual plan = planMensualService.create(dto);
        URI uri = UriComponentsBuilder
                .fromPath("/{id}")
                .buildAndExpand(plan.getId())
                .toUri();
        return ResponseEntity.created(uri).body(plan);
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<PlanMensual> updatePlan(@PathVariable Long id, @RequestBody DtoUpdatePlanMensual dto) {
        PlanMensual planActualizado = planMensualService.update(id, dto);
        return ResponseEntity.ok(planActualizado);
    }
}
