package com.covielloDevs.SistemaDeVerificacion.services;

import com.covielloDevs.SistemaDeVerificacion.models.evento.Evento;
import com.covielloDevs.SistemaDeVerificacion.models.evento.dto.DtoCreateEvento;
import com.covielloDevs.SistemaDeVerificacion.models.evento.dto.DtoDatosEvento;
import com.covielloDevs.SistemaDeVerificacion.models.evento.dto.DtoUpdateEvento;
import com.covielloDevs.SistemaDeVerificacion.models.licencia.Licencia;
import com.covielloDevs.SistemaDeVerificacion.models.usuario.Usuario;
import com.covielloDevs.SistemaDeVerificacion.repositories.EventoRepository;
import com.covielloDevs.SistemaDeVerificacion.repositories.UsuarioRepository;
import com.covielloDevs.SistemaDeVerificacion.utils.enums.EstadoEvento;
import com.covielloDevs.SistemaDeVerificacion.utils.enums.Rol;
import com.covielloDevs.SistemaDeVerificacion.utils.exceptions.evento.EventoNotFoundException;
import com.covielloDevs.SistemaDeVerificacion.utils.exceptions.licencia.LicenciaNoActivaException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class  EventoService {

    private final EventoRepository eventoRepository;
    private final PDFService pdfService;
    private final LicenciaService licenciaService;
    private final UsuarioRepository usuarioRepository;
    private final DataReaderService dataReaderService;
    public EventoService(EventoRepository eventoRepository, PDFService pdfService,
                         LicenciaService licenciaService, UsuarioRepository usuarioRepository,
                         DataReaderService dataReaderService) {
        this.eventoRepository = eventoRepository;
        this.pdfService = pdfService;
        this.licenciaService = licenciaService;
        this.usuarioRepository = usuarioRepository;
        this.dataReaderService = dataReaderService;
    }

    @Transactional
    public DtoDatosEvento createEvento(Long usuarioId, DtoCreateEvento eventoDto){
        Usuario usuario = usuarioRepository.findByIdWithLicencias(usuarioId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if(usuario.getRol().equals(Rol.DEV)) return createEventoDev(usuario.getId(), eventoDto);

        List<Licencia> licencias = usuario.getLicencias().stream().filter(Licencia::getActiva).toList();
        if(licencias.isEmpty()) throw new LicenciaNoActivaException("El usuario no posee licencias activas");
        licenciaService.useLicencia(licencias.get(new Random().nextInt(licencias.size())).getToken());
        Evento evento = new Evento(eventoDto);
        evento.setAnfitrion(usuario);
        evento.setEstadoEvento(EstadoEvento.ACTIVO); // <-- SOLUCIÓN
        return new DtoDatosEvento(eventoRepository.save(evento));
    }

    public Evento updateEvento(Evento evento){
        return eventoRepository.save(evento);
    }

    public Map<String, String> uploadListaParticipantes(@PathVariable Long id, MultipartFile file) throws Exception {
        Evento evento = findEventoById(id);
        evento.setListaParticipantes(dataReaderService.saveFile(file));
        updateEvento(evento);
        return Map.of("url", evento.getListaParticipantes());
    }

    public DtoDatosEvento updateEvento(DtoUpdateEvento evento, Long id){
        Evento eventoToUpdate = findEventoById(id);

        if(evento.nombre() != null) eventoToUpdate.setNombre(evento.nombre());
        if(evento.fecha() != null) eventoToUpdate.setFecha(evento.fecha());
        if(evento.itinerario() != null) eventoToUpdate.setItinerario(evento.itinerario());

        eventoRepository.save(eventoToUpdate);

        return new DtoDatosEvento(eventoToUpdate);
    }

    public void addItinerario(MultipartFile file, Long id) throws Exception {
        String path = pdfService.save(file);
        Evento evento = findEventoById(id);
        evento.setItinerario(path);
        updateEvento(evento);
    }

    @Transactional(readOnly = true)
    public DtoDatosEvento getEvento(Long id){
        return new DtoDatosEvento(findEventoById(id));
    }

    public Evento findEventoById(Long id){
        return eventoRepository.findById(id)
                .orElseThrow(() -> new EventoNotFoundException(String.format("Evento con id %s no encontrado", id)));
    }

    public Page<DtoDatosEvento> getAll(Pageable pageable){
        return eventoRepository.findAll(pageable).map(DtoDatosEvento::new);
    }

    public List<DtoDatosEvento> getllActive(Usuario usuario){
        return eventoRepository.findByAnfitrionAndEstadoEvento(usuario, EstadoEvento.ACTIVO)
                .stream().map(DtoDatosEvento::new).toList();
    }

    public void finishEvento(Long id){
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new EventoNotFoundException(String.format("Evento con id %s no encontrado", id)));
        evento.setEstadoEvento(EstadoEvento.FINALIZADO);
        updateEvento(evento);
    }

    @Transactional
    private DtoDatosEvento createEventoDev(Long usuarioId, DtoCreateEvento eventoDto){
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Evento evento = new Evento(eventoDto);
        evento.setAnfitrion(usuario);
        evento.setEstadoEvento(EstadoEvento.ACTIVO); // <-- SOLUCIÓN
        return new DtoDatosEvento(eventoRepository.save(evento));
    }
}
