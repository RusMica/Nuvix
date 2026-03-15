package com.covielloDevs.SistemaDeVerificacion.models.participante;

import com.covielloDevs.SistemaDeVerificacion.models.base.Persona;
import com.covielloDevs.SistemaDeVerificacion.models.entrada.Entrada;
import com.covielloDevs.SistemaDeVerificacion.models.evento.Evento;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "participantes")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Participante extends Persona {
    @OneToMany(mappedBy = "participante", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Entrada> entradas = new HashSet<>();
    private Boolean entradaActiva;
    @ManyToMany(mappedBy = "participantes", fetch = FetchType.LAZY)
    private Set<Evento> eventos = new HashSet<>();
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "participantes")
    private List<Movimiento> movimientos = new ArrayList<>();
    @PrePersist
    protected void onCreate(){
        this.entradaActiva = false;
    }
}
