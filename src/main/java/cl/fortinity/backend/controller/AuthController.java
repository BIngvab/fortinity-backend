package cl.fortinity.backend.controller;

import cl.fortinity.backend.model.Usuario;
import cl.fortinity.backend.model.Rol;
import cl.fortinity.backend.service.UsuarioService;
import cl.fortinity.backend.security.JwtService;
import cl.fortinity.backend.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.security.Principal;
import java.util.Locale;
import java.nio.charset.StandardCharsets;


@RestController
@RequestMapping({"/api/auth", "/api/v1/auth"})
public class AuthController {

    private final UsuarioService usuarioService;


    private final UsuarioRepository usuarioRepository;


    private final PasswordEncoder passwordEncoder;


    private final JwtService jwtService;


    public AuthController(
            UsuarioService usuarioService,
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;

    }


    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthRespuesta registrar(
            @RequestBody RegistroRequest datos) {

        Usuario usuario = usuarioService.registrar(
                datos.nombre(),
                datos.email(),
                datos.password());


        return generarRespuesta(usuario);

    }


    @PostMapping("/login")
    public AuthRespuesta login(
            @RequestBody LoginRequest datos) {

        if (datos.email() == null
                || datos.password() == null
                || datos.password()
                        .getBytes(StandardCharsets.UTF_8).length > 72) {

            throw credencialesInvalidas();

        }


        Usuario usuario = usuarioRepository.findByEmail(
                datos.email().trim().toLowerCase(Locale.ROOT))
                .orElseThrow(this::credencialesInvalidas);


        if (!usuario.isActivo()
                || !passwordEncoder.matches(
                        datos.password(), usuario.getPassword())) {

            throw credencialesInvalidas();

        }


        return generarRespuesta(usuario);

    }


    @GetMapping("/me")
    public UsuarioRespuesta perfil(Principal principal) {

        Usuario usuario = usuarioService.buscarPorEmail(
                principal.getName());


        return UsuarioRespuesta.desde(usuario);

    }


    private AuthRespuesta generarRespuesta(Usuario usuario) {

        return new AuthRespuesta(
                jwtService.generarToken(usuario),
                UsuarioRespuesta.desde(usuario));

    }


    private ResponseStatusException credencialesInvalidas() {

        return new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Correo o contrasena incorrectos");

    }


    public record RegistroRequest(

            String nombre,

            String email,

            String password

    ) {
    }


    public record LoginRequest(

            String email,

            String password

    ) {
    }


    public record AuthRespuesta(

            String token,

            UsuarioRespuesta usuario

    ) {
    }


    public record UsuarioRespuesta(

            Long id,

            String nombre,

            String email,

            Rol rol

    ) {

        public static UsuarioRespuesta desde(Usuario usuario) {

            return new UsuarioRespuesta(
                    usuario.getId(),
                    usuario.getNombre(),
                    usuario.getEmail(),
                    usuario.getRol());

        }

    }
}