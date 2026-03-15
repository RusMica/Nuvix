package com.covielloDevs.SistemaDeVerificacion.config.payment.mercadopago;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.PreferenceClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
public class PagoMercadopagoConfig {

    @Value("${mercadopago.access.token:${MERCADOPAGO_ACCESS_TOKEN:}}")
    private String accessToken;
    @Value("${mercadopago.sponsor.id:${MERCADOPAGO_SPONSOR_ID:}}")
    @Bean
    public PreferenceClient preferenceClient(){
        if (!StringUtils.hasText(accessToken)) {
            throw new IllegalStateException("MercadoPago access token no configurado." +
                    " Defina 'mercadopago.access.token' o variable de entorno MERCADOPAGO_ACCESS_TOKEN.");
        }
        MercadoPagoConfig.setAccessToken(accessToken);
        
        return new PreferenceClient();
    }

    @Bean
    public PaymentClient paymentClient(){
        return new PaymentClient();
    }
}
