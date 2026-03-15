package com.covielloDevs.SistemaDeVerificacion.services.security;

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
public class JwtService {

    @Value("${token.secret}")
    private String secret;
    @Value("${token.expiration.hours}")
    private Long expiration;

    public String generateToken(UserDetails userDetails){
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("kav-api")
                    .withSubject(userDetails.getUsername())
                    .withExpiresAt(ExpirationDate.generate(expiration, TipoDuracion.HORA))
                    .sign(algorithm);

        } catch (JWTCreationException exception){
            throw new TokenBadRequestException("Error al generar token");
        }
    }

    public String validateToken(String token){
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("kav-api")
                    .build();
            DecodedJWT decodedJWT = verifier.verify(token);
            return decodedJWT.getSubject();
        } catch (JWTVerificationException exception){
            throw new TokenBadRequestException("Token inválido");
        }
    }

}
