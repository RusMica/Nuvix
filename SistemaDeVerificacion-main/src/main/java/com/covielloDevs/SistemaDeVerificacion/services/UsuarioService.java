package com.covielloDevs.SistemaDeVerificacion.services;

import com.covielloDevs.SistemaDeVerificacion.models.usuario.Usuario;
import com.covielloDevs.SistemaDeVerificacion.models.usuario.dto.DtoDatosUsuario;
import com.covielloDevs.SistemaDeVerificacion.repositories.UsuarioRepository;
import com.covielloDevs.SistemaDeVerificacion.services.storage.SupabaseStorageService;
import com.covielloDevs.SistemaDeVerificacion.utils.exceptions.codigoVerificacion.CodigoVerificacionInvalidoException;
import com.covielloDevs.SistemaDeVerificacion.utils.exceptions.usuario.UsuarioEmailDuplicateException;
import com.covielloDevs.SistemaDeVerificacion.utils.exceptions.usuario.UsuarioNotFoundException;
import com.covielloDevs.SistemaDeVerificacion.utils.saveFiles.SaveImage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final SaveImage saveImage;
    private final EmailCodeService emailCodeService;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, SaveImage saveImage,
                          EmailCodeService emailCodeService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.saveImage = saveImage;
        this.emailCodeService = emailCodeService;
    }

    public Usuario getUser(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNotFoundException(String.format("El usuario con id %s no exite", id)));
    }

    public DtoDatosUsuario getByEmail(String email){
        return usuarioRepository.findByEmail(email).map(DtoDatosUsuario::new)
                .orElseThrow(() ->
                        new UsuarioNotFoundException(String.format("El usuario con email %s no exite", email)));
    }

    public Optional<Usuario> getUserByUsername(String username){
        return usuarioRepository.findByEmail(username);
    }

    public Page<Usuario> getAllUsers(Pageable pageable) {
        return usuarioRepository.findAll(pageable);
    }

    public Usuario createUser(Usuario usuario) {

        if(usuarioRepository.existsByEmail(usuario.getEmail()))
            throw new UsuarioEmailDuplicateException("El email ingresado ya existe");

        String passwordEncoded = passwordEncoder.encode(usuario.getPassword());
        usuario.setUsername(usuario.getEmail());
        usuario.setPassword(passwordEncoded);
        return usuarioRepository.save(usuario);
    }

    public Usuario updateUserAdmin(Usuario usuario, long id) {
        Usuario userToUpdate = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNotFoundException(String.format("Usuario con id %s no encontrado", id)));


        if (usuario.getEmail() != null) {
            usuarioRepository.findByEmail(usuario.getEmail()).ifPresent(u -> {
                if (!u.getId().equals(id)) {
                    throw new UsuarioEmailDuplicateException(String.format("El email %s ya pertenece a otro usuario"
                                                                                                , usuario.getEmail()));
                }
            });
            userToUpdate.setEmail(usuario.getEmail());
            userToUpdate.setUsername(usuario.getEmail());
        }

        if(usuario.getTelefono() != null) userToUpdate.setTelefono(usuario.getTelefono());
        if(usuario.getRol() != null) userToUpdate.setRol(usuario.getRol());

        return usuarioRepository.save(userToUpdate);
    }

    public Usuario updateUser(Usuario usuario, long id){
        Usuario userToUpdate = getUser(id);

        if(usuario.getEmail() != null) userToUpdate
                .setEmail(usuario.getEmail());
        if(usuario.getTelefono() != null) userToUpdate
                .setTelefono(usuario.getTelefono());

        return usuarioRepository.save(userToUpdate);
    }

    public void updateUser(Long id){
        usuarioRepository.save(getUser(id));
    }

    public Usuario addFoto(Long id, MultipartFile foto) throws Exception {
        if(foto.isEmpty())
            throw new IllegalArgumentException("La imagen es requerida");
        var usuario = usuarioRepository.findById(id);
        if(usuario.isEmpty())
            throw new UsuarioNotFoundException(String.format("Usuario con id %s no encontrado", id));

        usuario.get().setFoto(saveImage.save(foto));

        return usuarioRepository.save(usuario.get());
    }

    public void enableDisableUser(long id, boolean enable) {
        var user = usuarioRepository.findById(id);
        if(user.isEmpty())
            throw new UsuarioNotFoundException(String.format("Usuario con id %s no encontrado", id));

        if (enable)
            user.get().setActivo(true);
        if (!enable)
            user.get().setActivo(false);

        usuarioRepository.save(user.get());
    }

    public void deleteUser(long id) {
        if(usuarioRepository.findById(id).isEmpty())
            throw new UsuarioNotFoundException(String.format("Usuario con id %s no encontrado", id));
        usuarioRepository.deleteById(id);
    }

    public void verifyCode(String email, String code){
        if (!emailCodeService.validate(email, code)) throw new CodigoVerificacionInvalidoException("Codigo inválido");
    }

    public void changePassword(String email, String newPassword) {
        Optional<Usuario> usuario = usuarioRepository.findByEmail(email);
        if (usuario.isEmpty())
            throw new UsuarioNotFoundException(String.format("No existe usuario con email %s", email));
        usuario.get().setPassword(passwordEncoder.encode(newPassword));
        usuarioRepository.save(usuario.get());
    }
}