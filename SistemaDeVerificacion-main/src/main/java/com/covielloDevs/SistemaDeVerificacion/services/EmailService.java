package com.covielloDevs.SistemaDeVerificacion.services;

import com.covielloDevs.SistemaDeVerificacion.utils.exceptions.email.EmailSendException;
import com.covielloDevs.SistemaDeVerificacion.models.dto.DtoEmail;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender javaMailSender;

    @Value("${app.mail.enabled:true}")
    private boolean mailEnabled;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void enviarEmail(DtoEmail email) throws MessagingException {
        if (!mailEnabled) {
            log.warn("Email sending is disabled (app.mail.enabled=false). Skipping email to {} with subject '{}'", email.destinatario(), email.asunto());
            return;
        }
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(email.destinatario());
            helper.setSubject(email.asunto());
            helper.setText(email.mensaje(), true);

            if (email.qrCode() != null && email.qrCodeName() != null) {
                helper.addAttachment(email.qrCodeName(), new ByteArrayResource(email.qrCode()));
            }

            if (email.pdf() != null && email.pdfName() != null) {
                helper.addAttachment(email.pdfName(), new ByteArrayResource(email.pdf()));
            }

            javaMailSender.send(message);
        } catch (MailAuthenticationException e) {
            // Typical case when Gmail user/pass or App Password is invalid
            String msg = "Falló la autenticación con el servidor de correo. Verifique MAIL_USER/MAIL_PASSWORD (App Password de Gmail) o desactive el envío con app.mail.enabled=false.";
            log.error(msg + " Causa: {}", e.getMessage());
            throw new EmailSendException(msg, e);
        } catch (Exception e) {
            String msg = "No se pudo enviar el email por un error inesperado.";
            log.error(msg, e);
            throw new EmailSendException(msg, e);
        }
    }
}
