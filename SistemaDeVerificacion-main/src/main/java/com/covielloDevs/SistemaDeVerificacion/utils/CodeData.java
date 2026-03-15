package com.covielloDevs.SistemaDeVerificacion.utils;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "code")
public class CodeData {
    private String code;
    private LocalDateTime expiresAt;
}
