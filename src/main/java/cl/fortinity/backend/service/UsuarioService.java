package cl.fortinity.backend.service;

import cl.fortinity.backend.model.Usuario;
import cl.fortinity.backend.model.Rol;
import cl.fortinity.backend.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Locale;
import java.nio.charset.StandardCharsets;


@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;


    private final PasswordEncoder passwordEncoder;


    public UsuarioService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {

        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;

    }


    public List<Usuario> listar() {
        return usuarioRepository.findAll();

    }


    public Usuario buscarPorId(Long id) {

        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuario no encontrado"));

    }


    public Usuario buscarPorEmail(String email) {

        return usuarioRepository.findByEmail(
                email.trim().toLowerCase(Locale.ROOT))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuario no encontrado"));

    }


    public Usuario registrar(
            String nombre, String email, String password) {

        if (nombre == null || nombre.isBlank()
                || nombre.trim().length() > 100) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Nombre invalido");

        }


        if (email == null || email.length() > 150
                || !email.trim().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Correo electronico invalido");

        }


        if (password == null || password.length() < 8
                || password.getBytes(StandardCharsets.UTF_8).length > 72) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La contrasena debe tener al menos 8 caracteres y un maximo de 72 bytes");

        }


        email = email.trim().toLowerCase(Locale.ROOT);


        if (usuarioRepository.existsByEmail(email)) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El correo ya esta registrado");

        }


        Usuario usuario = new Usuario();

        
        usuario.setNombre(nombre.trim());
        usuario.setEmail(email);
        usuario.setPassword(passwordEncoder.encode(password));
        usuario.setRol(Rol.CLIENTE);
        usuario.setActivo(true);


        return usuarioRepository.save(usuario);

    }


    public Usuario cambiarRol(Long id, Rol rol) {

        if (rol == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Rol invalido");

        }


        Usuario usuario = buscarPorId(id);


        usuario.setRol(rol);


        return usuarioRepository.save(usuario);

    }


    public Usuario cambiarEstado(Long id, boolean activo) {

        Usuario usuario = buscarPorId(id);


        usuario.setActivo(activo);


        return usuarioRepository.save(usuario);

    }
}