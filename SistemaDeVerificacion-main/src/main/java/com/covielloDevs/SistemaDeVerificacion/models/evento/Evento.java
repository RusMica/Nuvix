package com.covielloDevs.SistemaDeVerificacion.models.evento;

import com.covielloDevs.SistemaDeVerificacion.models.evento.dto.DtoCreateEvento;
import com.covielloDevs.SistemaDeVerificacion.models.evento.dto.DtoDatosEvento;
import com.covielloDevs.SistemaDeVerificacion.models.participante.Participante;
import com.covielloDevs.SistemaDeVerificacion.models.usuario.Usuario;
import com.covielloDevs.SistemaDeVerificacion.utils.enums.EstadoEvento;
import com.covielloDevs.SistemaDeVerificacion.utils.enums.TamanoEvento;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "eventos")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Evento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private LocalDate fecha;
    private String itinerario;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "anfitrion_id")
    private Usuario anfitrion;
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "participante_evento",
            joinColumns = @JoinColumn(name = "evento_id"),
            inverseJoinColumns = @JoinColumn(name = "participante_id")
    )
    private Set<Participante> participantes = new HashSet<>();
    private String listaParticipantes;
    @Enumerated(EnumType.STRING)
    private EstadoEvento estadoEvento;
    private TamanoEvento tamanoEvento;
    @PrePersist
    protected void onCreate(){
        this.estadoEvento = EstadoEvento.ACTIVO;
    }

    public Evento(DtoCreateEvento evento) {
        this.nombre = evento.nombre();
        this.fecha = evento.fecha();
    }

    public Evento(DtoDatosEvento evento) {
        this.nombre = evento.nombre();
        this.fecha = evento.fecha();
    }
}
