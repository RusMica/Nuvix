package com.covielloDevs.SistemaDeVerificacion.services;

import com.covielloDevs.SistemaDeVerificacion.models.participante.Participante;
import com.covielloDevs.SistemaDeVerificacion.models.participante.Movimiento;
import com.covielloDevs.SistemaDeVerificacion.models.participante.dto.DtoMovimiento;
import com.covielloDevs.SistemaDeVerificacion.repositories.MovimientoRepository;
import com.covielloDevs.SistemaDeVerificacion.utils.enums.TipoIngreso;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MovimientoService {
    
    private final MovimientoRepository movimientoRepository;
    private final ParticipanteService participanteService;
    public MovimientoService(MovimientoRepository movimientoRepository, ParticipanteService participanteService) {
        this.movimientoRepository = movimientoRepository;
        this.participanteService = participanteService;
    }

    public Page<DtoMovimiento> getMovimientosByParticipanteId(Long id, Pageable pageable){
        var participante = participanteService.findParticipanteById(id);
        return movimientoRepository.findAllByParticipantes_Id(participante.getId(), pageable);
    }

    @Transactional
    public void createMovimiento(Participante participante, TipoIngreso tipoIngreso){
        var movimiento = new Movimiento();
        movimiento.setTipoIngreso(tipoIngreso);
        movimiento.getParticipantes().add(participante);
        participante.getMovimientos().add(movimiento);
        movimientoRepository.save(movimiento);
    }
}
