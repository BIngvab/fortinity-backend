package cl.fortinity.backend.controller;

import cl.fortinity.backend.model.Boleta;
import cl.fortinity.backend.model.DetalleBoleta;
import cl.fortinity.backend.model.Usuario;
import cl.fortinity.backend.model.Rol;
import cl.fortinity.backend.dto.CompraItemDTO;
import cl.fortinity.backend.service.BoletaService;
import cl.fortinity.backend.service.UsuarioService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.security.Principal;
import java.util.List;


@RestController
@RequestMapping({"/api/boletas", "/api/v1/boletas"})
@Transactional
public class BoletaController {

    private final BoletaService boletaService;


    private final UsuarioService usuarioService;


    public BoletaController(
            BoletaService boletaService,
            UsuarioService usuarioService) {

        this.boletaService = boletaService;
        this.usuarioService = usuarioService;

    }


    @GetMapping
    public List<BoletaRespuesta> listarTodas(Principal principal) {

        Usuario usuario = usuarioActual(principal);

        if (usuario.getRol() != Rol.ADMIN
                && usuario.getRol() != Rol.VENDEDOR) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Acceso denegado");

        }

        return boletaService.listarTodas()
                .stream()
                .map(BoletaRespuesta::desde)
                .toList();

    }


    @GetMapping("/mis-compras")
    public List<BoletaRespuesta> misCompras(Principal principal) {

        Usuario usuario = usuarioActual(principal);

        return boletaService.listarPorUsuario(usuario.getId())
                .stream()
                .map(BoletaRespuesta::desde)
                .toList();

    }


    @GetMapping("/{id}")
    public BoletaRespuesta buscarPorId(
            @PathVariable Long id,
            Principal principal) {

        Usuario usuario = usuarioActual(principal);

        Boleta boleta = boletaService.buscarPorId(id);

        verificarAcceso(boleta, usuario);

        return BoletaRespuesta.desde(boleta);

    }


    @GetMapping("/{id}/detalles")
    public List<DetalleRespuesta> listarDetalles(
            @PathVariable Long id,
            Principal principal) {

        Usuario usuario = usuarioActual(principal);

        Boleta boleta = boletaService.buscarPorId(id);

        verificarAcceso(boleta, usuario);

        return boletaService.listarDetalles(id)
                .stream()
                .map(DetalleRespuesta::desde)
                .toList();

    }


    @PostMapping("/comprar")
    @ResponseStatus(HttpStatus.CREATED)
    public BoletaRespuesta comprar(
            @RequestBody List<CompraItemDTO> items,
            Principal principal) {

        Usuario usuario = usuarioActual(principal);

        if (usuario.getRol() != Rol.CLIENTE) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Solo los clientes pueden realizar compras");

        }

        return BoletaRespuesta.desde(
                boletaService.comprar(usuario.getId(), items));

    }


    private Usuario usuarioActual(Principal principal) {

        if (principal == null) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Debes iniciar sesion");

        }

        Usuario usuario = usuarioService.buscarPorEmail(
                principal.getName());

        if (!usuario.isActivo()) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "La cuenta esta desactivada");

        }

        return usuario;

    }


    private void verificarAcceso(Boleta boleta, Usuario usuario) {

        boolean propietario = boleta.getUsuario()
                .getId().equals(usuario.getId());

        boolean autorizado = usuario.getRol() == Rol.ADMIN
                || usuario.getRol() == Rol.VENDEDOR;

        if (!propietario && !autorizado) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "No tienes permiso para consultar esta boleta");

        }

    }


    public record BoletaRespuesta(

            Long id,

            Long usuarioId,

            String cliente,

            BigDecimal total,

            String estado,

            LocalDateTime fecha

    ) {

        public static BoletaRespuesta desde(Boleta boleta) {

            return new BoletaRespuesta(
                    boleta.getId(),
                    boleta.getUsuario().getId(),
                    boleta.getUsuario().getNombre(),
                    boleta.getTotal(),
                    boleta.getEstado(),
                    boleta.getFecha());

        }

    }


    public record DetalleRespuesta(

            Long id,

            Long productoId,

            String producto,

            int cantidad,

            BigDecimal precioUnitario,

            BigDecimal subtotal

    ) {

        public static DetalleRespuesta desde(DetalleBoleta detalle) {

            return new DetalleRespuesta(
                    detalle.getId(),
                    detalle.getProducto().getId(),
                    detalle.getProducto().getNombre(),
                    detalle.getCantidad(),
                    detalle.getPrecioUnitario(),
                    detalle.getSubtotal());

        }
    }
}