package com.covielloDevs.SistemaDeVerificacion.controllers;

import com.covielloDevs.SistemaDeVerificacion.models.dto.DtoQRToken;
import com.covielloDevs.SistemaDeVerificacion.models.usuario.Usuario;
import com.covielloDevs.SistemaDeVerificacion.services.EntradaService;
import com.covielloDevs.SistemaDeVerificacion.services.ParticipanteService;
import com.covielloDevs.SistemaDeVerificacion.services.qr.QRCodeService;
import com.covielloDevs.SistemaDeVerificacion.services.qr.QRJwtService;
import com.covielloDevs.SistemaDeVerificacion.services.security.UserDetailsService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/v1/qr")
public class QRController {

    private final QRJwtService qrJwtService;
    private final UserDetailsService userDetailsService;
    private final QRCodeService qrCodeService;
    private final ParticipanteService participanteService;
    private final EntradaService entradaService;

    public QRController(QRJwtService qrJwtService, UserDetailsService userDetailsService,
                        QRCodeService qrCodeService, ParticipanteService participanteService,
                        EntradaService entradaService) {
        this.qrJwtService = qrJwtService;
        this.userDetailsService = userDetailsService;
        this.qrCodeService = qrCodeService;
        this.participanteService = participanteService;
        this.entradaService = entradaService;
    }

    @GetMapping("/generate-qr/{id}")
    public ResponseEntity<Void> generateQR(@PathVariable Long id) throws Exception {
        var participante = participanteService.findParticipanteById(id);
        var entrada = entradaService.findEntradaByParticipante(participante);
        qrCodeService.generateQRCode(entrada.getToken().toString());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/generate-qr-token")
    public ResponseEntity<Void> generateQrToken(@AuthenticationPrincipal Usuario usuario)
                                                                throws Exception {
        String qrToken = qrJwtService.generateToken(usuario);
        qrCodeService.generateQRCode(qrToken);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/generate-token")
    public ResponseEntity<Map<String, String>> generateToken(HttpServletResponse response,
                                                @AuthenticationPrincipal Usuario usuario){
        String qrToken = qrJwtService.generateToken(usuario);
        return ResponseEntity.ok(Map.of("qrToken", qrToken));
    }

    @GetMapping("/validate-qr")
    public ResponseEntity<Map<String, String>> validateToken(@RequestParam("qr-token") DtoQRToken qrToken){
        String username = qrJwtService.validateToken(qrToken.qrToken());
        Usuario usuario = (Usuario) userDetailsService.loadUserByUsername(username);

        return ResponseEntity.ok(Map.of("message", "Registro creado"));
    }
}
