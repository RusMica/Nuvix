package com.covielloDevs.SistemaDeVerificacion.repositories;

import com.covielloDevs.SistemaDeVerificacion.models.Setting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

@Repository
public interface SettingRepository extends JpaRepository<Setting, String> {
    Optional<Setting> findSettingValueBySettingKey(String settingKey);
}