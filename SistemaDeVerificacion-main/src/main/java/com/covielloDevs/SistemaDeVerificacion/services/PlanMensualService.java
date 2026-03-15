package com.covielloDevs.SistemaDeVerificacion.services;

import com.covielloDevs.SistemaDeVerificacion.models.Setting;
import com.covielloDevs.SistemaDeVerificacion.models.planMensual.PlanMensual;
import com.covielloDevs.SistemaDeVerificacion.models.planMensual.dto.DtoCreatePlanMensual;
import com.covielloDevs.SistemaDeVerificacion.models.planMensual.dto.DtoUpdatePlanMensual;
import com.covielloDevs.SistemaDeVerificacion.repositories.PlanMensualRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PlanMensualService {


    private final PlanMensualRepository planMensualRepository;
    private final SettingService settingService;

    public PlanMensualService(PlanMensualRepository planMensualRepository, SettingService settingService) {
        this.planMensualRepository = planMensualRepository;
        this.settingService = settingService;
    }

    public List<PlanMensual> getPlanes(){
        return planMensualRepository.findAll();
    }

    @Transactional
    public PlanMensual create(DtoCreatePlanMensual dto) {
        Setting precioSetting = settingService.getSettingValueBySettingKey(dto.precioSettingKey());
        PlanMensual plan = new PlanMensual(dto, precioSetting);
        return planMensualRepository.save(plan);
    }

    @Transactional
    public PlanMensual update(Long id, DtoUpdatePlanMensual dto) {
        PlanMensual plan = planMensualRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Plan no encontrado con ID: " + id));
        plan.actualizar(dto);
        return plan;
    }
}
