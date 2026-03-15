package com.covielloDevs.SistemaDeVerificacion.repositories;

import com.covielloDevs.SistemaDeVerificacion.models.planMensual.PlanMensual;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanMensualRepository extends JpaRepository<PlanMensual, Long> {
}
