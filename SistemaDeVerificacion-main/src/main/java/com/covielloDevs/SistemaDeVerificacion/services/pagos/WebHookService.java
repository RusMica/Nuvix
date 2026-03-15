package com.covielloDevs.SistemaDeVerificacion.services.pagos;

import com.covielloDevs.SistemaDeVerificacion.models.PagoProcesado;
import com.covielloDevs.SistemaDeVerificacion.models.planMensual.PlanMensual;
import com.covielloDevs.SistemaDeVerificacion.models.usuario.Usuario;
import com.covielloDevs.SistemaDeVerificacion.repositories.PlanMensualRepository;
import com.covielloDevs.SistemaDeVerificacion.repositories.ProcessedPaymentRepository;
import com.covielloDevs.SistemaDeVerificacion.services.LicenciaService;
import com.covielloDevs.SistemaDeVerificacion.services.UsuarioService;
import com.covielloDevs.SistemaDeVerificacion.utils.enums.Rol;
import com.covielloDevs.SistemaDeVerificacion.utils.enums.TipoPreferencia;
import com.covielloDevs.SistemaDeVerificacion.utils.exceptions.usuario.UsuarioNotFoundException;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.payment.PaymentItem;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
public class WebHookService {

    private static final Logger log = LoggerFactory.getLogger(WebHookService.class);

    private final PagoMercadopagoService pagoMercadopagoService;
    private final LicenciaService licenciaService;
    private final ProcessedPaymentRepository processedPaymentRepository;
    private final UsuarioService usuarioService;
    private final PlanMensualRepository planMensualRepository;

    public WebHookService(PagoMercadopagoService pagoMercadopagoService, LicenciaService licenciaService,
                          ProcessedPaymentRepository processedPaymentRepository, UsuarioService usuarioService,
                          PlanMensualRepository planMensualRepository) {
        this.pagoMercadopagoService = pagoMercadopagoService;
        this.licenciaService = licenciaService;
        this.processedPaymentRepository = processedPaymentRepository;
        this.usuarioService = usuarioService;
        this.planMensualRepository = planMensualRepository;
    }

    public void handleWebHookNotification(Map<String, String> notification)
            throws MPException, MPApiException, MessagingException {
        String topic = notification.getOrDefault("topic", notification.get("type"));

        if (!"payment".equals(topic)) {
            log.info(">>> Notificación recibida de tipo '{}'. Ignorando.", topic);
            return;
        }

        String paymentId = notification.get("id");
        if (paymentId == null) paymentId = notification.get("data.id");
        if (paymentId == null || paymentId.isBlank()) return;

        log.info(">>> Procesando notificación para el ID de pago: {}", paymentId);
        Payment payment = pagoMercadopagoService.getPaymentDetails(Long.parseLong(paymentId));

        if ("approved".equals(payment.getStatus())) {
            log.info(">>> Pago APROBADO para el usuario: {}", payment.getPayer().getEmail());
            processApprovedPayment(payment);
        } else {
            log.info(">>> El pago {} no está aprobado. Estado actual: {}", paymentId, payment.getStatus());
        }
    }

    @Transactional
    public void processApprovedPayment(Payment payment) throws MessagingException {
        Long paymentId = payment.getId();
        String userEmail = payment.getPayer().getEmail();
        int cantidad;
        if (processedPaymentRepository.existsById(paymentId)) {
            log.warn(">>> El pago con ID {} ya fue procesado. Ignorando notificación duplicada.", paymentId);
            return;
        }

        Usuario usuario = usuarioService.getUserByUsername(userEmail)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuario no encontrado con email: " + userEmail));

        processedPaymentRepository.save(new PagoProcesado(paymentId, LocalDateTime.now()));
        log.info(">>> Pago con ID {} guardado como procesado.", paymentId);

        for (PaymentItem item : payment.getAdditionalInfo().getItems()) {
            String itemId = item.getId();
            cantidad = item.getQuantity();
            if (itemId.startsWith("SUSCRIPCION_PLAN_")) {
                TipoPreferencia tipo = TipoPreferencia.valueOf(itemId);
                long planId = (long) tipo.ordinal() + 1;
                PlanMensual plan = planMensualRepository.findById(planId)
                        .orElseThrow(() -> new NoSuchElementException("Plan no encontrado con ID: " + planId));

                usuario.setPlanMensual(plan);
                usuario.setFechaVencimiento(LocalDateTime.now().plusMonths(cantidad));
                log.info(">>> Plan {} asignado al usuario {}. Vencimiento: {}.",
                        plan.getNombre(), userEmail, usuario.getFechaVencimiento());

            } else if (itemId.startsWith("LIC-PREPAGA-")) {
                cantidad = item.getQuantity();
                for (int i = 0; i < cantidad; i++) {
                    licenciaService.createLicencia(userEmail);
                }
                log.info(">>> {} Licencia/s creada/s para el usuario: {}", cantidad, userEmail);
            }
        }

        if (usuario.getRol().equals(Rol.USER_TRIAL)) usuario.setRol(Rol.USER_PAID);

        usuarioService.updateUser(usuario.getId());
    }
}
