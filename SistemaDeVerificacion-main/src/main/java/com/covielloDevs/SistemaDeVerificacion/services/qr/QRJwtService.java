package com.covielloDevs.SistemaDeVerificacion.services.qr;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.covielloDevs.SistemaDeVerificacion.utils.ExpirationDate;
import com.covielloDevs.SistemaDeVerificacion.utils.enums.TipoDuracion;
import com.covielloDevs.SistemaDeVerificacion.utils.exceptions.token.TokenBadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class QRJwtService {
    @Value("${qr.token.secret}")
    private String secret;
    @Value("${qr.token.expiration.minutes}")
    private Long expiration;

    public String generateToken(UserDetails userDetails){
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("kav-user-entry-request")
                    .withSubject(userDetails.getUsername())
                    .withClaim("purpose", "QR_ENTRY_REQUEST")
                    .withExpiresAt(ExpirationDate.generate(expiration, TipoDuracion.MINUTO))
                    .sign(algorithm);

        } catch (JWTCreationException exception){
            throw new TokenBadRequestException("Error al generar token QR");
        }
    }

    public String validateToken(String token){
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("kav-user-entry-request")
                    .withClaim("purpose", "QR_ENTRY_REQUEST")
                    .build();
            DecodedJWT decodedJWT = verifier.verify(token);
            return decodedJWT.getSubject();
        } catch (JWTVerificationException exception){
            throw new TokenBadRequestException("Token inválido");
        }
    }

}
