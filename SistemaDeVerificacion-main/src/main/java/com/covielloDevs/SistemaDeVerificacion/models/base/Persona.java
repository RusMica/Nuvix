package com.covielloDevs.SistemaDeVerificacion.models.base;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@MappedSuperclass
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class Persona {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JsonProperty("Apellido")
    private String apellido;
    @JsonProperty("Nombre")
    private String nombre;
    @JsonProperty("DNI")
    private String dni;
    @JsonProperty("Email")
    private String email;
    @JsonProperty("Telefono")
    private String telefono;
}
