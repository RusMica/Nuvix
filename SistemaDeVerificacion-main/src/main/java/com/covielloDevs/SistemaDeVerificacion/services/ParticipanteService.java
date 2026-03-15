package com.covielloDevs.SistemaDeVerificacion.services;

import com.covielloDevs.SistemaDeVerificacion.models.dto.DtoEmail;
import com.covielloDevs.SistemaDeVerificacion.models.evento.Evento;
import com.covielloDevs.SistemaDeVerificacion.models.participante.Participante;
import com.covielloDevs.SistemaDeVerificacion.models.participante.dto.DtoDatosParticipante;
import com.covielloDevs.SistemaDeVerificacion.models.participante.dto.DtoDatosParticipantesWithEstadoEntrada;
import com.covielloDevs.SistemaDeVerificacion.repositories.ParticipanteRepository;
import com.covielloDevs.SistemaDeVerificacion.services.qr.QRCodeService;
import com.covielloDevs.SistemaDeVerificacion.services.storage.SupabaseStorageService;
import com.covielloDevs.SistemaDeVerificacion.utils.exceptions.email.EmailBadRequestException;
import com.covielloDevs.SistemaDeVerificacion.utils.exceptions.participante.ParticipanteNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@Service
public class ParticipanteService {

    private final ParticipanteRepository participanteRepository;
    private final DataReaderService dataReaderService;
    private final EntradaService entradaService;
    private final QRCodeService qrCodeService;
    private final EmailService emailService;
    private final EventoService eventoService;
    private final SupabaseStorageService supabaseStorageService;

    private static final Logger logger = LoggerFactory.getLogger(ParticipanteService.class);

    public ParticipanteService(ParticipanteRepository participanteRepository,
                               DataReaderService dataReaderService, @Lazy EntradaService entradaService,
                               QRCodeService qrCodeService, EmailService emailService, EventoService eventoService,
                               SupabaseStorageService supabaseStorageService) {
        this.participanteRepository = participanteRepository;
        this.dataReaderService = dataReaderService;
        this.entradaService = entradaService;
        this.qrCodeService = qrCodeService;
        this.emailService = emailService;
        this.eventoService = eventoService;
        this.supabaseStorageService = supabaseStorageService;
    }

    public DtoDatosParticipante getParticipante(Long id){
        return new DtoDatosParticipante(findParticipanteById(id));
    }

    public Participante findParticipanteById(Long id){
        return participanteRepository.findById(id)
                .orElseThrow(() -> new ParticipanteNotFoundException("Participante no encontrado"));
    }

    public List<Participante> findAllParticipanteByEvento(Long id){
        return participanteRepository.findAllByEventos_Id(id);
    }

    public Page<DtoDatosParticipantesWithEstadoEntrada> findListaByEvento(Long id, Pageable pageable){
        return participanteRepository.findAllByEventos_Id(id, pageable)
                .map(p -> new DtoDatosParticipantesWithEstadoEntrada(p,
                                            entradaService.findEntradaByParticipante(p))
        );
    }

    @Transactional
    public Map<String, String> createParticipante(Long idEvento, String filePath) throws Exception {
        // 1. Validación defensiva contra datos incorrectos del cliente
        if (filePath == null || filePath.isBlank() || filePath.contains("[object Object]")) {
            throw new IllegalArgumentException("La ruta del archivo es inválida. El valor recibido fue: " + filePath);
        }

        Evento evento = eventoService.findEventoById(idEvento);
        // Descargar archivo desde Supabase
        byte[] fileBytes = supabaseStorageService.download(filePath);
        // Obtener el nombre del archivo (última parte del path)
        String fileName = filePath.contains("/")
                ? filePath.substring(filePath.lastIndexOf("/") + 1) : filePath;
        // Leer datos desde los bytes
        String data = dataReaderService.readDataFromBytes(fileBytes, fileName);
        List<Participante> participantes = dataReaderService.formatList(data);

        participanteRepository.saveAll(participantes);
        evento.getParticipantes().addAll(participantes);
        participantes.forEach(p -> p.getEventos().add(evento));

        Evento savedEvento = eventoService.updateEvento(evento);

        savedEvento.getParticipantes().forEach(entradaService::createEntrada);

        // 2. Llamada al método asíncrono para no bloquear la respuesta al usuario
        sendEmailsToParticipants(savedEvento, participantes);

        return Map.of("message", "Participantes creados. El envío de correos se está procesando en segundo plano.");
    }

    @Async
    public void sendEmailsToParticipants(Evento evento, List<Participante> participantes) throws Exception {
        for (Participante participante : participantes)
            sendEmail(evento, participante, getItinerario(evento));
    }

    public void sendEmail(Evento evento, Participante participante, byte[] itinerarioBytes) throws Exception {
        String codigoEntrada = entradaService.findEntradaByParticipante(participante).getToken().toString();
        byte[] qrCode = qrCodeService.generateQRCode(codigoEntrada);
        String mensaje = "<p>Hola " + participante.getNombre() + ",</p>" +
                "<p>Adjuntamos tu código QR para el evento. ¡No lo compartas!</p>" +
                "<p>Saludos,</p>" +
                "<p>El equipo organizador</p>";

        emailService.enviarEmail(new DtoEmail(participante.getEmail(), "Tu entrada para el evento '"
                + evento.getNombre() + "'"
                , mensaje, qrCode
                ,"codigo-qr.png", getItinerario(evento), "itinerario.pdf"));

    }

    private byte[] getItinerario(Evento evento){
        if (evento.getItinerario() != null && !evento.getItinerario().isBlank())
            return supabaseStorageService.download(evento.getItinerario());
        throw new RuntimeException("No se encontró el itinerario del evento");
    }

     public List<DtoDatosParticipante> getByEvento(Long idEvento){
        return findAllParticipanteByEvento(idEvento).stream().map(DtoDatosParticipante::new).toList();
     }
}
