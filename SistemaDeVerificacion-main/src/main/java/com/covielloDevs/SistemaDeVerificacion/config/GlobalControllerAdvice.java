package com.covielloDevs.SistemaDeVerificacion.config;

import com.covielloDevs.SistemaDeVerificacion.utils.exceptions.email.EmailSendException;
import com.covielloDevs.SistemaDeVerificacion.utils.exceptions.codigoVerificacion.CodigoVerificacionInvalidoException;
import com.covielloDevs.SistemaDeVerificacion.utils.exceptions.email.EmailBadRequestException;
import com.covielloDevs.SistemaDeVerificacion.utils.exceptions.entrada.EntradaInvalidException;
import com.covielloDevs.SistemaDeVerificacion.utils.exceptions.entrada.EntradaNotFoundException;
import com.covielloDevs.SistemaDeVerificacion.utils.exceptions.licencia.LicenciaNoActivaException;
import com.covielloDevs.SistemaDeVerificacion.utils.exceptions.licencia.LicenciaNotFoundException;
import com.covielloDevs.SistemaDeVerificacion.utils.exceptions.movimiento.MovimientoNotFoundException;
import com.covielloDevs.SistemaDeVerificacion.utils.exceptions.participante.ParticipanteNotFoundException;
import com.covielloDevs.SistemaDeVerificacion.utils.exceptions.token.TokenBadRequestException;
import com.covielloDevs.SistemaDeVerificacion.utils.exceptions.usuario.UsuarioDniDuplicateException;
import com.covielloDevs.SistemaDeVerificacion.utils.exceptions.usuario.UsuarioEmailDuplicateException;
import com.covielloDevs.SistemaDeVerificacion.utils.exceptions.usuario.UsuarioNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(value = UsuarioEmailDuplicateException.class)
    public ResponseEntity<String> usuarioEmailDuplicateException(UsuarioEmailDuplicateException e){
        return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
    }
    @ExceptionHandler(value = UsuarioDniDuplicateException.class)
    public ResponseEntity<String> usuarioDniDuplicateException(UsuarioDniDuplicateException e){
        return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = UsuarioNotFoundException.class)
    public ResponseEntity<String> usuarioNotFoundException(UsuarioNotFoundException e){
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = MovimientoNotFoundException.class)
    public ResponseEntity<String> movimientoException(MovimientoNotFoundException e){
        return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = TokenBadRequestException.class)
    public ResponseEntity<String> tokenBadRequestException(TokenBadRequestException e){
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = EmailBadRequestException.class)
    public ResponseEntity<String> emailBadRequestException(EmailBadRequestException e){
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = LicenciaNotFoundException.class)
    public ResponseEntity<String> licenciaNotFoundException(LicenciaNotFoundException e){
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = LicenciaNoActivaException.class)
    public ResponseEntity<String> licenciaNoActivaException(LicenciaNoActivaException e){
        return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = ParticipanteNotFoundException.class)
    public ResponseEntity<String> participanteNotFoundException(ParticipanteNotFoundException e){
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = EntradaNotFoundException.class)
    public ResponseEntity<String> entradaNotFoundException(EntradaNotFoundException e){
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = CodigoVerificacionInvalidoException.class)
    public ResponseEntity<String> codigoVerificacionInvalidoException(CodigoVerificacionInvalidoException e){
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmailSendException.class)
    public ResponseEntity<Map<String, String>> EmailSendException(EmailSendException e) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(Map.of(
                        "error", "EMAIL_SENDING_FAILED",
                        "message", e.getMessage()
                ));
    }

    @ExceptionHandler(value = EntradaInvalidException.class)
    public ResponseEntity<String> entradaInvalidaException(EntradaInvalidException e){
        return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
    }
}
