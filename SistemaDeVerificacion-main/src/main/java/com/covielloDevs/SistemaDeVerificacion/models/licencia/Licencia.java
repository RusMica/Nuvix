package com.covielloDevs.SistemaDeVerificacion.models.licencia;
import com.covielloDevs.SistemaDeVerificacion.models.usuario.Usuario;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "licencias")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Licencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private UUID token;
    private Boolean activa;
    private LocalDate fechaInicio;
    private LocalDate fechaExpiracion;
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @PrePersist
    protected void onCreate(){
        this.token = UUID.randomUUID();
        this.activa = true;
        fechaInicio = LocalDate.now();
        fechaExpiracion = LocalDate.now().plusYears(1);
    }
}
