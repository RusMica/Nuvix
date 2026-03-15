package com.covielloDevs.SistemaDeVerificacion.models.horario;

import com.covielloDevs.SistemaDeVerificacion.utils.enums.Dia;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"dia", "hora"})
@Embeddable
public class Horario{
    @Enumerated(EnumType.STRING)
    private Dia dia;
    private LocalTime hora;
}