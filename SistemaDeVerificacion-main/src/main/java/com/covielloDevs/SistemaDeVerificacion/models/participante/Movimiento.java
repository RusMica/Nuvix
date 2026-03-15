package com.covielloDevs.SistemaDeVerificacion.models.participante;

import com.covielloDevs.SistemaDeVerificacion.utils.enums.TipoIngreso;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "movimientos")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Movimiento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private TipoIngreso tipoIngreso;
    private LocalDateTime movimiento;
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "movimientos_participantes",
            joinColumns = @JoinColumn(name = "movimiento_id"),
            inverseJoinColumns = @JoinColumn(name = "participante_id")
    )
    private List<Participante> participantes = new ArrayList<>();
    @PrePersist
    protected void onCreate(){
        this.movimiento = LocalDateTime.now();
    }
}
