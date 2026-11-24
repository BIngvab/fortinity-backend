package cl.fortinity.backend.service;

import cl.fortinity.backend.model.Boleta;
import cl.fortinity.backend.model.DetalleBoleta;
import cl.fortinity.backend.model.Producto;
import cl.fortinity.backend.model.Usuario;
import cl.fortinity.backend.dto.CompraItemDTO;
import cl.fortinity.backend.repository.BoletaRepository;
import cl.fortinity.backend.repository.DetalleBoletaRepository;
import cl.fortinity.backend.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.ArrayList;


@Service
@Transactional
public class BoletaService {

    private final BoletaRepository boletaRepository;


    private final DetalleBoletaRepository detalleBoletaRepository;


    private final ProductoRepository productoRepository;


    private final UsuarioService usuarioService;


    public BoletaService(
            BoletaRepository boletaRepository,
            DetalleBoletaRepository detalleBoletaRepository,
            ProductoRepository productoRepository,
            UsuarioService usuarioService) {

        this.boletaRepository = boletaRepository;
        this.detalleBoletaRepository = detalleBoletaRepository;
        this.productoRepository = productoRepository;
        this.usuarioService = usuarioService;

    }


    public List<Boleta> listarTodas() {
        return boletaRepository.findAllByOrderByFechaDesc();

    }


    public List<Boleta> listarPorUsuario(Long usuarioId) {
        return boletaRepository.findByUsuarioIdOrderByFechaDesc(usuarioId);

    }


    public Boleta buscarPorId(Long id) {

        return boletaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Boleta no encontrada"));

    }


    public List<DetalleBoleta> listarDetalles(Long boletaId) {

        buscarPorId(boletaId);

        return detalleBoletaRepository.findByBoletaId(boletaId);

    }


    public Boleta comprar(
            Long usuarioId,
            List<CompraItemDTO> items) {

        if (items == null || items.isEmpty()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El carrito esta vacio");

        }


        Usuario usuario = usuarioService.buscarPorId(usuarioId);


        if (!usuario.isActivo()) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "La cuenta esta desactivada");

        }


        Boleta boleta = new Boleta();

        boleta.setUsuario(usuario);
        boleta.setEstado("COMPLETADA");


        BigDecimal total = BigDecimal.ZERO;


        List<DetalleBoleta> detalles = new ArrayList<>();


        for (CompraItemDTO item : items) {

            if (item == null || item.productoId() == null
                    || item.cantidad() <= 0) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Producto o cantidad invalida");

            }


            Producto producto = productoRepository
                    .buscarParaCompra(item.productoId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Producto no encontrado"));


            if (!producto.isActivo()) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "El producto no esta disponible");

            }


            if (producto.getStock() < item.cantidad()) {

                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Stock insuficiente para " + producto.getNombre());

            }


            BigDecimal descuento = BigDecimal
                    .valueOf(producto.getDescuento())
                    .divide(BigDecimal.valueOf(100));


            BigDecimal precioFinal = producto.getPrecio()
                    .multiply(BigDecimal.ONE.subtract(descuento))
                    .setScale(2, RoundingMode.HALF_UP);


            BigDecimal subtotal = precioFinal
                    .multiply(BigDecimal.valueOf(item.cantidad()));


            DetalleBoleta detalle = new DetalleBoleta();


            detalle.setBoleta(boleta);
            detalle.setProducto(producto);
            detalle.setCantidad(item.cantidad());
            detalle.setPrecioUnitario(precioFinal);
            detalle.setSubtotal(subtotal);


            detalles.add(detalle);


            producto.setStock(
                    producto.getStock() - item.cantidad());


            productoRepository.save(producto);


            total = total.add(subtotal);

        }


        boleta.setTotal(total);


        Boleta boletaGuardada = boletaRepository.save(boleta);


        detalleBoletaRepository.saveAll(detalles);


        return boletaGuardada;

    }
}