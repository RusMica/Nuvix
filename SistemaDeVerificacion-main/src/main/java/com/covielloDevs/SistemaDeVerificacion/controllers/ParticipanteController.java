package com.covielloDevs.SistemaDeVerificacion.controllers;

import com.covielloDevs.SistemaDeVerificacion.models.evento.Evento;
import com.covielloDevs.SistemaDeVerificacion.models.participante.Participante;
import com.covielloDevs.SistemaDeVerificacion.models.participante.dto.DtoDatosParticipante;
import com.covielloDevs.SistemaDeVerificacion.models.participante.dto.DtoDatosParticipantesWithEstadoEntrada;
import com.covielloDevs.SistemaDeVerificacion.services.EventoService;
import com.covielloDevs.SistemaDeVerificacion.services.ParticipanteService;
import com.covielloDevs.SistemaDeVerificacion.services.storage.SupabaseStorageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/participantes")
public class ParticipanteController {

    private final ParticipanteService participanteService;
    private final SupabaseStorageService storageService;
    private final EventoService eventoService;
    public ParticipanteController(ParticipanteService participanteService, SupabaseStorageService storageService,
                                  EventoService eventoService) {
        this.participanteService = participanteService;
        this.storageService = storageService;
        this.eventoService = eventoService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<DtoDatosParticipante> getParticipante(@PathVariable Long id) {
        return ResponseEntity.ok(participanteService.getParticipante(id));
    }

    @GetMapping("/evento/{idEvento}")
    public ResponseEntity<List<DtoDatosParticipante>> getByEvento(@PathVariable Long idEvento) {
        return ResponseEntity.ok(participanteService.getByEvento(idEvento));
    }

    @GetMapping("/evento/list/{idEvento}")
    public ResponseEntity<Page<DtoDatosParticipantesWithEstadoEntrada>> getByEvento(@PathVariable Long idEvento,
                                                                                    Pageable pageable) {
        return ResponseEntity.ok(participanteService.findListaByEvento(idEvento, pageable));
    }

    @PostMapping("/create/{idEvento}")
    public ResponseEntity<Map<String, String>> createParticipante(@PathVariable Long idEvento,
                                                                  @RequestParam("filePath") String filePath)
            throws Exception {
        return ResponseEntity.ok(participanteService.createParticipante(idEvento, filePath));
    }

    @PostMapping("/resend-emails/{eventoId}")
    public ResponseEntity<Map<String, String>> resendEmails(@PathVariable Long eventoId) throws Exception {
        Evento evento = eventoService.findEventoById(eventoId);
        List<Participante> participantes = participanteService.findAllParticipanteByEvento(evento.getId());
        participanteService.sendEmailsToParticipants(evento, participantes);
        return ResponseEntity.ok(Map.of("message", "Entradas reenviadas exitosamente"));
    }

    @PostMapping("/resend-emails/{eventoId}/{participanteId}")
    public ResponseEntity<Map<String,String>> resendEmails(@PathVariable Long eventoId,
                                                           @PathVariable Long participanteId) throws Exception {
        Evento evento = eventoService.findEventoById(eventoId);
        Participante participante = participanteService.findParticipanteById(participanteId);
        participanteService.sendEmail(evento, participante, null);
        return ResponseEntity.ok(Map.of("message", "Entrada reenviada exitosamente"));
    }
    @GetMapping("/download-template")
    public ResponseEntity<byte[]> downloadTemplate(){
        byte[] fileBytes = storageService.download("plantilla_participantes.xlsx");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "plantilla_participantes.xlsx");
        return ResponseEntity.ok().headers(headers).body(fileBytes);
    }
}
