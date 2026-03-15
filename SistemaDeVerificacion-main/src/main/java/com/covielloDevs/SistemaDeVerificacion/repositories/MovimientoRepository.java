package com.covielloDevs.SistemaDeVerificacion.repositories;

import com.covielloDevs.SistemaDeVerificacion.models.participante.Movimiento;
import com.covielloDevs.SistemaDeVerificacion.models.participante.dto.DtoMovimiento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {

    Page<DtoMovimiento> findAllByParticipantes_Id(Long id, Pageable pageable);
}
