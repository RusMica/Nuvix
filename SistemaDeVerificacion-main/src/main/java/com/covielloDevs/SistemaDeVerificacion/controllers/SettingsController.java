package com.covielloDevs.SistemaDeVerificacion.controllers;

import com.covielloDevs.SistemaDeVerificacion.models.Setting;
import com.covielloDevs.SistemaDeVerificacion.models.dto.DtoUpdateSetting;
import com.covielloDevs.SistemaDeVerificacion.services.SettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/settings")
public class SettingsController {

    @Autowired
    private SettingService settingService;

    @GetMapping("/all")
    public ResponseEntity<List<Setting>> getAllSettings() {
        return ResponseEntity.ok(settingService.getAllSettings());
    }

    @PutMapping("/{key}")
    @Transactional
    public ResponseEntity<Setting> updateSetting(@PathVariable String key, @RequestBody DtoUpdateSetting dto) {
        Setting updatedSetting = settingService.updateSetting(key, dto.value());
        return ResponseEntity.ok(updatedSetting);
    }
}
