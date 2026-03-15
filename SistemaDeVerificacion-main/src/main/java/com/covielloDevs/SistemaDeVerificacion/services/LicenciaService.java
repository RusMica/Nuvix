package com.covielloDevs.SistemaDeVerificacion.services;

import com.covielloDevs.SistemaDeVerificacion.models.licencia.Licencia;
import com.covielloDevs.SistemaDeVerificacion.models.usuario.Usuario;
import com.covielloDevs.SistemaDeVerificacion.repositories.LicenciaRepository;
import com.covielloDevs.SistemaDeVerificacion.utils.enums.Rol;
import com.covielloDevs.SistemaDeVerificacion.utils.exceptions.licencia.LicenciaNotFoundException;
import com.covielloDevs.SistemaDeVerificacion.utils.exceptions.usuario.UsuarioNotFoundException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class LicenciaService {

    private final LicenciaRepository licenciaRepository;
    private final UsuarioService usuarioService;
    public LicenciaService(LicenciaRepository licenciaRepository, UsuarioService usuarioService) {
        this.licenciaRepository = licenciaRepository;
        this.usuarioService = usuarioService;
    }

    public void createLicencia(String email){
        Usuario usuario = usuarioService.getUserByUsername(email)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuario no encontrado"));
        Licencia licencia = new Licencia();
        licencia.setUsuario(usuario);
        licencia.setFechaExpiracion(LocalDate.now().plusMonths(1));
        licenciaRepository.save(licencia);
        usuario.getLicencias().add(licencia);
    }

    public void useLicencia(UUID token){
        Licencia licencia = licenciaRepository.findByToken(token)
                .orElseThrow(() -> new LicenciaNotFoundException("Licencia no encontrada"));
        disableLicencia(licencia);
    }

    public Licencia getLicenciaByUsuario(Usuario usuario){
        return licenciaRepository.findByUsuario(usuario)
                .orElseThrow(() -> new LicenciaNotFoundException("Licencia no encontrada"));
    }

    public Boolean licenciaIsExpired(Licencia licencia){
        return licenciaRepository.isLicenciaExpired(licencia.getToken().toString(), LocalDate.now());
    }

    private void disableLicencia(Licencia licencia){
        licencia.setActiva(false);
        licenciaRepository.save(licencia);
    }

    @Scheduled(fixedRate = 86400000)
    private void eliminarLicenciasUsadasYVencidas() {
        List<Licencia> licenciasVencidas = licenciaRepository.findLicenciasVencidas(LocalDate.now());
        List<Licencia> licenciasUsadas = licenciaRepository.findByActiva(false);

        Set<Licencia> licenciasAEliminar = new HashSet<>();
        licenciasAEliminar.addAll(licenciasVencidas);
        licenciasAEliminar.addAll(licenciasUsadas);

        licenciaRepository.deleteAll(licenciasAEliminar);
    }
}
