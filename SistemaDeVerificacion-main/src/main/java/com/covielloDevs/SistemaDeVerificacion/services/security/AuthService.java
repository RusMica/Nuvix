package com.covielloDevs.SistemaDeVerificacion.services.security;

import com.covielloDevs.SistemaDeVerificacion.models.dto.DtoAuthUser;
import com.covielloDevs.SistemaDeVerificacion.models.evento.Evento;
import com.covielloDevs.SistemaDeVerificacion.models.usuario.Usuario;
import com.covielloDevs.SistemaDeVerificacion.models.usuario.dto.DtoRegistroUsuario;
import com.covielloDevs.SistemaDeVerificacion.services.EmailCodeService;
import com.covielloDevs.SistemaDeVerificacion.services.UsuarioService;
import com.covielloDevs.SistemaDeVerificacion.utils.enums.EstadoEvento;
import com.covielloDevs.SistemaDeVerificacion.utils.enums.Rol;
import com.covielloDevs.SistemaDeVerificacion.utils.exceptions.usuario.UsuarioNotFoundException;
import jakarta.mail.MessagingException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final UsuarioService usuarioService;
    private final EmailCodeService emailCodeService;

    public AuthService(AuthenticationManager authenticationManager, UserDetailsService userDetailsService,
                       JwtService jwtService, UsuarioService usuarioService, EmailCodeService emailCodeService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
        this.usuarioService = usuarioService;
        this.emailCodeService = emailCodeService;
    }

    public String authenticate(DtoAuthUser request){
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(),
                request.password()));
        UserDetails user = userDetailsService.loadUserByUsername(request.email());
        Usuario usuario = usuarioService.getUserByUsername(request.email())
                .orElseThrow(() -> new UsuarioNotFoundException("Usuario no encontrado"));

        if(updateUserRoleIfNeeded(usuario))
            user = userDetailsService.loadUserByUsername(request.email());

        return jwtService.generateToken(user);
    }

    public Map<String, String> register(DtoRegistroUsuario dtoRegistroUsuario) {
        Usuario usuario = usuarioService.createUser(new Usuario(dtoRegistroUsuario));

        return Map.of("message", String.format("Usuario %s creado con exito", usuario.getUsername()));
    }

    public Map<String, String> requestReset(String email) throws MessagingException {
        emailCodeService.generateCode(email);
        return Map.of("message", "Email enviado exitosamente");
    }

    private boolean updateUserRoleIfNeeded(Usuario usuario){
        // Si el usuario ya es TRIAL o es ADMIN, no hay nada que hacer.
        if (usuario.getRol() == Rol.USER_TRIAL || usuario.getRol() == Rol.ADMIN
                                                || usuario.getRol() == Rol.DEV) return false;

        // Condición 1: No debe haber eventos activos para considerar una degradación.
        final boolean isEventosInactivos = usuario.getEventos().stream().noneMatch(e ->
                                                        e.getEstadoEvento() == EstadoEvento.ACTIVO);

        boolean downgradable = false;

        switch (usuario.getRol()) {
            case USER_PAID:
                // Para el plan prepago (USER_PAID), se degrada si no tiene licencias y no hay eventos activos.
                final boolean noTieneLicencias = usuario.getLicencias().isEmpty();
                downgradable = isEventosInactivos && noTieneLicencias;
                break;
            case USER_PAID_MONTHLY_COMMON:
            case USER_PAID_MONTHLY_PROFESSIONAL:
                // Para planes mensuales con límites, se degrada si no hay eventos activos Y si el plan expiró o se agotaron las cuotas.
                if (usuario.getPlanMensual() == null) {
                    downgradable = true; // Si no tiene plan, se degrada.
                    break;
                }
                final boolean isPlanExpired = usuario.getFechaVencimiento().isBefore(LocalDateTime.now());
                final boolean isCantidadEventosZero = usuario.getPlanMensual().getCantidadEventos() <= 0;
                final boolean isCantidadInvitadosZero = usuario.getPlanMensual().getCantidadInvitados() <= 0;

                downgradable = isEventosInactivos && (isPlanExpired || isCantidadEventosZero || isCantidadInvitadosZero);
                break;
            case USER_PAID_MONTHLY_CORPORATE:
                // Para el plan CORPORATE, solo importa si el plan expiró, ya que no tiene otros límites.
                if (usuario.getPlanMensual() == null) {
                    downgradable = true; // Si no tiene plan, se degrada.
                    break;
                }
                final boolean isCorporatePlanExpired = usuario.getFechaVencimiento().isBefore(LocalDateTime.now());
                downgradable = isEventosInactivos && isCorporatePlanExpired;
                break;
        }

        if(downgradable){
            usuario.setRol(Rol.USER_TRIAL);
            usuarioService.updateUser(usuario.getId());
            return true;
        }
        return false;
    }
}
