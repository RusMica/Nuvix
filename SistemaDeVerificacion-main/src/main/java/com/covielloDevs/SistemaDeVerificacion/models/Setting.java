package com.covielloDevs.SistemaDeVerificacion.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "settingKey")
public class Setting {

    @Id
    private String settingKey;
    @Column(precision = 10, scale = 2)
    private BigDecimal settingValue;

}