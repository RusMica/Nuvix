package com.covielloDevs.SistemaDeVerificacion.repositories;

import com.covielloDevs.SistemaDeVerificacion.models.evento.Evento;
import com.covielloDevs.SistemaDeVerificacion.models.usuario.Usuario;
import com.covielloDevs.SistemaDeVerificacion.utils.enums.EstadoEvento;
import jdk.jfr.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;


public interface EventoRepository extends JpaRepository<Evento, Long> {
    List<Evento> findByAnfitrionAndEstadoEvento(Usuario anfitrion, EstadoEvento estadoEvento);

    /**
     * Busca eventos finalizados (fecha anterior a hoy) que todavía tengan rutas de archivo asociadas.
     */
    @Query("SELECT e FROM Evento e WHERE e.fecha < :today AND (e.itinerario IS NOT NULL OR e.listaParticipantes IS NOT NULL)")
    List<Evento> findFinishedEventsWithFiles(LocalDate today);

    /**
     * Devuelve una lista de todas las rutas de itinerarios y listas de participantes que no son nulas.
     */
    @Query("SELECT e.itinerario FROM Evento e WHERE e.itinerario IS NOT NULL UNION SELECT e.listaParticipantes FROM Evento e WHERE e.listaParticipantes IS NOT NULL")
    List<String> findAllUsedFilePaths();
}
