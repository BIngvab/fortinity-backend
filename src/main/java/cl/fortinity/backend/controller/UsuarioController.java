package cl.fortinity.backend.controller;

import cl.fortinity.backend.model.Usuario;
import cl.fortinity.backend.model.Rol;
import cl.fortinity.backend.service.UsuarioService;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;


    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;

    }


    @GetMapping
    public List<UsuarioRespuesta> listar() {

        return usuarioService.listar()
                .stream()
                .map(UsuarioRespuesta::desde)
                .toList();

    }


    @GetMapping("/{id}")
    public UsuarioRespuesta buscarPorId(@PathVariable Long id) {

        return UsuarioRespuesta.desde(
                usuarioService.buscarPorId(id));

    }


    @PatchMapping("/{id}/rol")
    public UsuarioRespuesta cambiarRol(
            @PathVariable Long id,
            @RequestParam Rol rol) {

        return UsuarioRespuesta.desde(
                usuarioService.cambiarRol(id, rol));

    }


    @PatchMapping("/{id}/estado")
    public UsuarioRespuesta cambiarEstado(
            @PathVariable Long id,
            @RequestParam boolean activo) {

        return UsuarioRespuesta.desde(
                usuarioService.cambiarEstado(id, activo));

    }


    public record UsuarioRespuesta(

            Long id,

            String nombre,

            String email,

            Rol rol,

            boolean activo,

            LocalDateTime fechaRegistro

    ) {

        public static UsuarioRespuesta desde(Usuario usuario) {

            return new UsuarioRespuesta(
                    usuario.getId(),
                    usuario.getNombre(),
                    usuario.getEmail(),
                    usuario.getRol(),
                    usuario.isActivo(),
                    usuario.getFechaRegistro());

        }
    }
}