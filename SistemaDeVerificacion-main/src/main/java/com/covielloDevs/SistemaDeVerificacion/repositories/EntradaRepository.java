package com.covielloDevs.SistemaDeVerificacion.repositories;

import com.covielloDevs.SistemaDeVerificacion.models.participante.Participante;
import com.covielloDevs.SistemaDeVerificacion.models.entrada.Entrada;
import com.covielloDevs.SistemaDeVerificacion.utils.enums.EstadoEntrada;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EntradaRepository extends JpaRepository<Entrada, Long> {

    Boolean existsByTokenAndEstado(UUID token, EstadoEntrada estado);

    Optional<Entrada> findByToken(UUID token);

    Optional<Entrada> findByParticipante(Participante participante);
}
