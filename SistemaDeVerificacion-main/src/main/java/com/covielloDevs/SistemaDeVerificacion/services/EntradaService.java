package com.covielloDevs.SistemaDeVerificacion.services;

import com.covielloDevs.SistemaDeVerificacion.models.entrada.Entrada;
import com.covielloDevs.SistemaDeVerificacion.models.entrada.dto.DtoDatosEntrada;
import com.covielloDevs.SistemaDeVerificacion.models.participante.Participante;
import com.covielloDevs.SistemaDeVerificacion.repositories.EntradaRepository;
import com.covielloDevs.SistemaDeVerificacion.utils.enums.EstadoEntrada;
import com.covielloDevs.SistemaDeVerificacion.utils.enums.TipoIngreso;
import com.covielloDevs.SistemaDeVerificacion.utils.exceptions.entrada.EntradaInvalidException;
import com.covielloDevs.SistemaDeVerificacion.utils.exceptions.entrada.EntradaNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class EntradaService {

    private final EntradaRepository entradaRepository;
    private final MovimientoService movimientoService;
    public EntradaService(EntradaRepository entradaRepository, MovimientoService movimientoService) {
        this.entradaRepository = entradaRepository;
        this.movimientoService = movimientoService;
    }

    @Transactional(readOnly = true)
    public DtoDatosEntrada getEntrada(Long id) {
        return new DtoDatosEntrada(findEntradaById(id));
    }

    public Entrada findEntradaById(Long id) {
        return entradaRepository.findById(id)
                .orElseThrow(() -> new EntradaNotFoundException("Entrada no encontrada con id: " + id));
    }

    public Entrada findEntradaByParticipante(Participante participante) {
        return entradaRepository.findByParticipante(participante)
                .orElseThrow(() ->
                        new EntradaNotFoundException(String.format("Entrada no encontrada para el participante: %s",
                                                                participante.getNombre() + participante.getNombre())));
    }

    private Entrada findEntradaByToken(UUID token) {
        return entradaRepository.findByToken(token)
                .orElseThrow(() -> new EntradaNotFoundException("Entrada no encontrada con token: " + token));
    }

    public void createEntrada(Participante participante) {
        var entrada = new Entrada();
        entrada.setParticipante(participante);
        entradaRepository.save(entrada);
    }

    @Transactional
    public DtoDatosEntrada validarEntrada(String tokenString) {
        UUID token = UUID.fromString(tokenString);
        Entrada entrada = findEntradaByToken(token);
        Participante participante = entrada.getParticipante();

        if (!participante.getEntradaActiva()) {
            entrada.setEstado(EstadoEntrada.USADO);
            participante.setEntradaActiva(true);
            movimientoService.createMovimiento(participante, TipoIngreso.ENTRADA);
        } else throw new EntradaInvalidException("Entrada ya usada");


        Entrada entradaActualizada = entradaRepository.save(entrada);
        return new DtoDatosEntrada(entradaActualizada);
    }

    @Transactional
    public DtoDatosEntrada validarSalida(String tokenString) {
        UUID token = UUID.fromString(tokenString);
        Entrada entrada = findEntradaByToken(token);
        Participante participante = entrada.getParticipante();

        if (participante.getEntradaActiva()){
            entrada.setEstado(EstadoEntrada.PENDIENTE);
            participante.setEntradaActiva(false);
            movimientoService.createMovimiento(participante, TipoIngreso.SALIDA);
        }else throw new EntradaInvalidException("Entrada pendiente");


        Entrada entradaActualizada = entradaRepository.save(entrada);
        return new DtoDatosEntrada(entradaActualizada);
    }
}
