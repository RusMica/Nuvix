package com.covielloDevs.SistemaDeVerificacion.services;

import com.covielloDevs.SistemaDeVerificacion.models.dto.DtoEmail;
import com.covielloDevs.SistemaDeVerificacion.utils.CodeData;
import jakarta.mail.MessagingException;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class EmailCodeService {

    private final SecureRandom secureRandom = new SecureRandom();
    private final Map<String, CodeData> codes = new HashMap<>();
    private final EmailService emailService;

    public EmailCodeService(EmailService emailService) {
        this.emailService = emailService;
    }

    public void generateCode(String email) throws MessagingException {
        int code = 100000 + new Random().nextInt(900000);
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(5);
        // Enviar email primero; solo guardar el código si el envío fue exitoso
        emailService.enviarEmail(new DtoEmail(email, "Código de verificación",
                "Su código de verificación es: " + code,
                null, null, null, null));
        codes.put(email, new CodeData(String.valueOf(code), expiresAt));
    }

    public Boolean validate(String email, String inputCode){
        CodeData stored = codes.get(email);
        if (stored == null) return false;
        if(stored.getCode().equals(inputCode) &&
                LocalDateTime.now().isBefore(stored.getExpiresAt()))
            codes.remove(email);
        return true;
    }
}