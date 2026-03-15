package com.covielloDevs.SistemaDeVerificacion.models.usuario;
import com.covielloDevs.SistemaDeVerificacion.models.evento.Evento;
import com.covielloDevs.SistemaDeVerificacion.models.licencia.Licencia;
import com.covielloDevs.SistemaDeVerificacion.models.planMensual.PlanMensual;
import com.covielloDevs.SistemaDeVerificacion.models.usuario.dto.DtoRegistroUsuario;
import com.covielloDevs.SistemaDeVerificacion.models.usuario.dto.DtoUpdateUsuario;
import com.covielloDevs.SistemaDeVerificacion.models.usuario.dto.DtoUpdateUsuarioAdmin;
import com.covielloDevs.SistemaDeVerificacion.utils.enums.Rol;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Usuario implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    @Size(min = 8, max = 255, message = "La contraseña debe tener un minimo de 8 caracteres")
    private String password;
    @Email
    @Column(unique = true)
    private String email;
    private String telefono;
    @Enumerated(EnumType.STRING)
    private Rol rol;
    private String foto;
    private Boolean activo;
    private LocalDateTime fechaVencimiento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_mensual_id")
    private PlanMensual planMensual;
    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Licencia> licencias = new HashSet<>();
    @OneToMany(mappedBy = "anfitrion", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Evento> eventos = new HashSet<>();

    public Usuario(DtoUpdateUsuarioAdmin usuario) {
        this.email = usuario.email();
        this.telefono = usuario.telefono();
        this.rol = usuario.rol();
    }

    public Usuario(DtoUpdateUsuario usuario) {
        this.email = usuario.email();
        this.telefono = usuario.telefono();
    }


    public Usuario(DtoRegistroUsuario dtoRegistroUsuario) {
        this.username = dtoRegistroUsuario.email();
        this.email = dtoRegistroUsuario.email();
        this.password = dtoRegistroUsuario.password();
    }

    @PrePersist
    protected void onCreate(){
        this.activo = true;
        if(this.rol == null) this.rol = Rol.USER_TRIAL;
        if (this.rol == Rol.USER_TRIAL) {
            this.fechaVencimiento = LocalDateTime.now().plusDays(7);
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + rol.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        if (this.fechaVencimiento == null) {
            return true; // O false, dependiendo de la logica de negocio.
        }
        return LocalDateTime.now().isBefore(this.fechaVencimiento);
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.activo;
    }
}
