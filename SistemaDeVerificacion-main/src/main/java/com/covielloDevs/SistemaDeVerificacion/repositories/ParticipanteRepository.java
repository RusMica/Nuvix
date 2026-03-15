package com.covielloDevs.SistemaDeVerificacion.repositories;

import com.covielloDevs.SistemaDeVerificacion.models.evento.Evento;
import com.covielloDevs.SistemaDeVerificacion.models.participante.Participante;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ParticipanteRepository extends JpaRepository<Participante, Long> {

    List<Participante> findAllByEventos_Id(Long eventoId);
    Page<Participante> findAllByEventos_Id(Long eventoId, Pageable pageable);
}
