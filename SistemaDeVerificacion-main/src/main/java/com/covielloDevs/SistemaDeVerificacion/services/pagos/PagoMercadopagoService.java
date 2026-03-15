package com.covielloDevs.SistemaDeVerificacion.services.pagos;

import com.covielloDevs.SistemaDeVerificacion.models.Setting;
import com.covielloDevs.SistemaDeVerificacion.models.dto.DtoCrearPreferenciaPago;
import com.covielloDevs.SistemaDeVerificacion.models.planMensual.PlanMensual;
import com.covielloDevs.SistemaDeVerificacion.repositories.PlanMensualRepository;
import com.covielloDevs.SistemaDeVerificacion.services.SettingService;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.preference.Preference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Map;

@Service
public class PagoMercadopagoService {

    private final PreferenceClient preferenceClient;
    private final SettingService settingService;
    private final PaymentClient paymentClient;
    private final PlanMensualRepository planMensualRepository;

    @Value("${mercadopago.default.image.url:}")
    private String defaultImageUrl;
    @Value("${1mercadopago.back.success.url:}")
    private String backSuccessUrl;
    @Value("${mercadopago.back.pending.url:}")
    private String backPendingUrl;
    @Value("${mercadopago.back.failure.url:}")
    private String backFailureUrl;
    @Value("${mercadopago.auto.return:approved}")
    private String autoReturnMode;
    @Value("${mercadopago.notification.path}")
    private String notificationPath;

    public PagoMercadopagoService(PreferenceClient preferenceClient,
                                  SettingService settingService,
                                  PaymentClient paymentClient,
                                  PlanMensualRepository planMensualRepository) {
        this.preferenceClient = preferenceClient;
        this.settingService = settingService;
        this.paymentClient = paymentClient;
        this.planMensualRepository = planMensualRepository;
    }

    public Payment getPaymentDetails(Long paymentId) throws MPException, MPApiException {
        return paymentClient.get(paymentId);
    }

    public Map<String, String> crearPreferenciaDePago(DtoCrearPreferenciaPago datosPreferencia)
            throws MPException, MPApiException, MalformedURLException {
        Preference preference = preferenceClient.create(construirPreferencia(datosPreferencia));
        return Map.of("preferenceId", preference.getId());
    }

    private PreferenceRequest construirPreferencia(DtoCrearPreferenciaPago datosPreferencia)
                                                                                        throws MalformedURLException {
        PreferenceRequest.PreferenceRequestBuilder builder = PreferenceRequest.builder()
                .items(List.of(crearItemPreferencia(datosPreferencia)))
                .backUrls(configureBackURLs());

        String baseUrl = getBaseUrl(backSuccessUrl);

        if(StringUtils.hasText(baseUrl)) builder.notificationUrl(baseUrl + notificationPath);

        if (StringUtils.hasText(getValidAutoReturnMode())) builder.autoReturn(getValidAutoReturnMode());

        return builder.build();
    }

    private PreferenceItemRequest crearItemPreferencia(DtoCrearPreferenciaPago datosPreferencia) {
        PreferenceItemRequest.PreferenceItemRequestBuilder item = PreferenceItemRequest.builder();
        item.currencyId("ARS");
        item.pictureUrl(defaultImageUrl);
        Setting precioLicenciaSetting;
        int cantidad = datosPreferencia.cantidad() != null && datosPreferencia.cantidad() > 0
                                                            ? datosPreferencia.cantidad() : 1;
        switch (datosPreferencia.tipoPreferencia()) {
            case SUSCRIPCION_PLAN_COMMON:
            case SUSCRIPCION_PLAN_PROFESSIONAL:
            case SUSCRIPCION_PLAN_CORPORATE:

                long planId = (long) (datosPreferencia.tipoPreferencia().ordinal() + 1);
                PlanMensual plan = planMensualRepository.findById(planId)
                        .orElseThrow(() -> new NoSuchElementException("Plan no encontrado con ID: " + planId));

                String description = String.format("Suscripción mensual al plan %s. Incluye hasta %d eventos y %d invitados por evento.",
                        plan.getNombre(), plan.getCantidadEventos(), plan.getCantidadInvitados());

                item.id(datosPreferencia.tipoPreferencia().name())
                        .title(plan.getNombre())
                        .description(description)
                        .quantity(1)
                        .unitPrice(plan.getPrecio().getSettingValue());
                break;

            case COMPRA_LICENCIA_PREPAGA_CHICA:
                precioLicenciaSetting = settingService.getSettingValueBySettingKey("PRECIO_LICENCIA_PREPAGA_CHICA");

                item.id("LIC-PREPAGA-CHICA")
                    .title("Licencia Prepaga Chica")
                    .description("Compra de licencias para eventos individuales.")
                    .quantity(cantidad)
                    .unitPrice(precioLicenciaSetting.getSettingValue());
                break;
            case COMPRA_LICENCIA_PREPAGA_MEDIANA:
                precioLicenciaSetting = settingService.getSettingValueBySettingKey("PRECIO_LICENCIA_PREPAGA_MEDIANA");

                item.id("LIC-PREPAGA-MEDIANA")
                        .title("Licencia Prepaga Mediana")
                        .description("Compra de licencias para eventos individuales.")
                        .quantity(cantidad)
                        .unitPrice(precioLicenciaSetting.getSettingValue());
                break;
            case COMPRA_LICENCIA_PREPAGA_GRANDE:
                precioLicenciaSetting = settingService.getSettingValueBySettingKey("PRECIO_LICENCIA_PREPAGA_GRANDE");

                item.id("LIC-PREPAGA-GRANDE")
                        .title("Licencia Prepaga Grande")
                        .description("Compra de licencias para eventos individuales.")
                        .quantity(cantidad)
                        .unitPrice(precioLicenciaSetting.getSettingValue());
                break;
            case COMPRA_LICENCIA_PREPAGA_MASIVA:
                precioLicenciaSetting = settingService.getSettingValueBySettingKey("PRECIO_LICENCIA_PREPAGA_MASIVA");

                item.id("LIC-PREPAGA-MASIVA")
                        .title("Licencia Prepaga Masiva")
                        .description("Compra de licencias para eventos individuales.")
                        .quantity(cantidad)
                        .unitPrice(precioLicenciaSetting.getSettingValue());
                break;
            default:
                throw new IllegalArgumentException("Tipo de preferencia no soportado: " + datosPreferencia.tipoPreferencia());
        }

        return item.build();
    }

    private PreferenceBackUrlsRequest configureBackURLs(){
        return PreferenceBackUrlsRequest.builder()
                .success(StringUtils.hasText(backSuccessUrl) ? backSuccessUrl : null)
                .pending(StringUtils.hasText(backPendingUrl) ? backPendingUrl : null)
                .failure(StringUtils.hasText(backFailureUrl) ? backFailureUrl : null)
                .build();
    }

    private String getValidAutoReturnMode() {
        if (!StringUtils.hasText(autoReturnMode)) return null;

        if (!StringUtils.hasText(backSuccessUrl) || !backSuccessUrl.startsWith("https")) return null;

        return autoReturnMode;
    }

    private String getBaseUrl(String fullUrl) throws MalformedURLException {
        if(!StringUtils.hasText(fullUrl)) return null;
        URL url = new URL(fullUrl);
        return url.getProtocol() + "://" + url.getHost();
    }
}
