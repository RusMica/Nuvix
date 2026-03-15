package com.covielloDevs.SistemaDeVerificacion.controllers;

import com.covielloDevs.SistemaDeVerificacion.models.evento.dto.DtoCreateEvento;
import com.covielloDevs.SistemaDeVerificacion.models.evento.dto.DtoDatosEvento;
import com.covielloDevs.SistemaDeVerificacion.models.evento.dto.DtoUpdateEvento;
import com.covielloDevs.SistemaDeVerificacion.models.usuario.Usuario;
import com.covielloDevs.SistemaDeVerificacion.services.EventoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/eventos")
public class EventoController {

    private final EventoService eventoService;

    public EventoController(EventoService eventoService) {
        this.eventoService = eventoService;
    }

    @GetMapping("/all")
    public ResponseEntity<Page<DtoDatosEvento>> getAllEvento(Pageable pageable){
        return ResponseEntity.ok(eventoService.getAll(pageable));
    }

    @GetMapping("/all/active")
    public ResponseEntity<List<DtoDatosEvento>> getAllActive(@AuthenticationPrincipal Usuario usuario){
        return ResponseEntity.ok(eventoService.getllActive(usuario));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DtoDatosEvento> getEvento(@PathVariable Long id){
        return ResponseEntity.ok(eventoService.getEvento(id));
    }

    @PostMapping
    public ResponseEntity<DtoDatosEvento> createEvento(@AuthenticationPrincipal Usuario usuario,
                                                       @RequestBody DtoCreateEvento evento){
        return ResponseEntity.ok(eventoService.createEvento(usuario.getId(), evento));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<DtoDatosEvento> updateEvento(@RequestBody DtoUpdateEvento evento, @PathVariable Long id) {
        return ResponseEntity.ok(eventoService.updateEvento(evento, id));
    }

    @PatchMapping("/add/itinerario/{id}")
    public ResponseEntity<DtoDatosEvento> addItinerario(@PathVariable Long id,
                                                        @RequestParam MultipartFile file) throws Exception {
        eventoService.addItinerario(file, id);
        return ResponseEntity.ok(eventoService.getEvento(id));
    }

    @PatchMapping("/add/lista/{id}")
    public ResponseEntity<Map<String, String>> uploadListaParticipantes(@PathVariable Long id,
                                                                        @RequestParam MultipartFile file)
            throws Exception {
        return ResponseEntity.ok(eventoService.uploadListaParticipantes(id, file));
    }
    @PatchMapping("/finish/{id}")
    public ResponseEntity<Map<String, String>> finishEvento(@PathVariable Long id){
        eventoService.finishEvento(id);
        return ResponseEntity.ok(Map.of("message", "Evento finalizado"));
    }
}
