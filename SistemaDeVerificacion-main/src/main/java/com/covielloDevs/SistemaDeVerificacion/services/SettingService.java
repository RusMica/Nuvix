package com.covielloDevs.SistemaDeVerificacion.services;

import com.covielloDevs.SistemaDeVerificacion.models.Setting;
import com.covielloDevs.SistemaDeVerificacion.repositories.SettingRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class SettingService {

    @Autowired
    private SettingRepository settingRepository;

    public Setting getSettingValueBySettingKey(String settingKey){
        return settingRepository.findById(settingKey)
                .orElseThrow(() -> new EntityNotFoundException("Setting not found with key: " + settingKey));
    }

    public List<Setting> getAllSettings(){
        return settingRepository.findAll();
    }

    @Transactional
    public Setting updateSetting(String key, BigDecimal value) {
        Setting setting = getSettingValueBySettingKey(key);
        setting.setSettingValue(value);
        return setting;
    }
}
