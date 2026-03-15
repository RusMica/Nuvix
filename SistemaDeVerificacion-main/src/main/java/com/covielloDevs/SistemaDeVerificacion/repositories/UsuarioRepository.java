package com.covielloDevs.SistemaDeVerificacion.repositories;

import com.covielloDevs.SistemaDeVerificacion.models.usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsername(String username);

    Optional<Usuario> findByEmail(String email);

    Boolean existsByEmail(String email);

    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.licencias WHERE u.id = :id")
    Optional<Usuario> findByIdWithLicencias(@Param("id") Long id);
}
