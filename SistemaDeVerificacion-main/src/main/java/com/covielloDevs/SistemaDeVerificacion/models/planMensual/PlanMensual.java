package com.covielloDevs.SistemaDeVerificacion.models.planMensual;

import com.covielloDevs.SistemaDeVerificacion.models.Setting;
import com.covielloDevs.SistemaDeVerificacion.models.planMensual.dto.DtoCreatePlanMensual;
import com.covielloDevs.SistemaDeVerificacion.models.planMensual.dto.DtoUpdatePlanMensual;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "planes_mensuales")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class PlanMensual {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private short cantidadEventos;
    private long cantidadInvitados;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "precio_setting_key")
    private Setting precio;

    public PlanMensual(DtoCreatePlanMensual dto, Setting precio) {
        this.nombre = dto.nombre();
        this.cantidadEventos = dto.cantidadEventos();
        this.cantidadInvitados = dto.cantidadInvitados();
        this.precio = precio;
    }

    public void actualizar(DtoUpdatePlanMensual dto) {
        this.cantidadEventos = dto.cantidadEventos();
        this.cantidadInvitados = dto.cantidadInvitados();
    }
}
