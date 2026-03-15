package com.covielloDevs.SistemaDeVerificacion.controllers;

import com.covielloDevs.SistemaDeVerificacion.models.dto.DtoAuthUser;
import com.covielloDevs.SistemaDeVerificacion.models.dto.DtoTokenUser;
import com.covielloDevs.SistemaDeVerificacion.models.usuario.dto.DtoChangePassword;
import com.covielloDevs.SistemaDeVerificacion.models.usuario.dto.DtoVerifyCode;
import com.covielloDevs.SistemaDeVerificacion.models.usuario.dto.DtoEmailRequest;
import com.covielloDevs.SistemaDeVerificacion.models.usuario.dto.DtoRegistroUsuario;
import com.covielloDevs.SistemaDeVerificacion.services.security.AuthService;
import com.covielloDevs.SistemaDeVerificacion.services.EmailCodeService;
import com.covielloDevs.SistemaDeVerificacion.services.UsuarioService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final EmailCodeService emailCodeService;
    private final UsuarioService usuarioService;

    public AuthController(AuthService authService, EmailCodeService emailCodeService, UsuarioService usuarioService) {
        this.authService = authService;
        this.emailCodeService = emailCodeService;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/login")
    public ResponseEntity<DtoTokenUser> authenticate(@RequestBody DtoAuthUser request){
        String token = authService.authenticate(request);
        return ResponseEntity.ok(new DtoTokenUser(token));
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody @Valid DtoRegistroUsuario request)
            throws MessagingException {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/send-code")
    public ResponseEntity<Map<String, String>> sendCode(@RequestBody @Valid DtoEmailRequest request)
            throws MessagingException {
        emailCodeService.generateCode(request.email());
        return ResponseEntity.ok(Map.of("message", "Email enviado exitosamente"));
    }

    @PostMapping("/verify-code")
    public ResponseEntity<Map<String, String>> verifyCode(@RequestBody @Valid DtoVerifyCode request) {
        usuarioService.verifyCode(request.email(), request.code());
        return ResponseEntity.ok(Map.of("message", "Contraseña cambiada exitosamente"));
    }

    @PostMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(@RequestBody @Valid DtoChangePassword request) {
        usuarioService.changePassword(request.email(), request.newPassword());
        return ResponseEntity.ok(Map.of("message", "Contraseña cambiada exitosamente"));
    }
}