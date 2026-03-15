package com.covielloDevs.SistemaDeVerificacion.repositories;

import com.covielloDevs.SistemaDeVerificacion.models.licencia.Licencia;
import com.covielloDevs.SistemaDeVerificacion.models.usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LicenciaRepository extends JpaRepository<Licencia, Long> {

    @Query("SELECT EXISTS(SELECT 1 FROM Licencia l WHERE l.token = :token AND l.fechaExpiracion < :currentDate)")
    boolean isLicenciaExpired(@Param("token") String token, @Param("currentDate") LocalDate currentDate);

    @Query("SELECT l FROM Licencia l WHERE l.activa = true AND l.fechaExpiracion < :currentDate")
    List<Licencia> findLicenciasVencidas(@Param("currentDate") LocalDate currentDate);

    Optional<Licencia> findByUsuario(Usuario usuario);

    List<Licencia> findByActiva(Boolean activa);

    Optional<Licencia> findByToken(UUID token);
}
