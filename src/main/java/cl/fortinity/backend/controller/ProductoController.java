package cl.fortinity.backend.controller;

import cl.fortinity.backend.model.Producto;
import cl.fortinity.backend.service.ProductoService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.math.BigDecimal;


@RestController
@RequestMapping("/api/productos")
@Transactional
public class ProductoController {

    private final ProductoService productoService;


    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;

    }


    @GetMapping
    public List<ProductoRespuesta> listar() {

        return productoService.listar()
                .stream()
                .map(ProductoRespuesta::desde)
                .toList();

    }


    @GetMapping("/todos")
    public List<ProductoRespuesta> listarTodos() {

        return productoService.listarTodos()
                .stream()
                .map(ProductoRespuesta::desde)
                .toList();

    }


    @GetMapping("/destacados")
    public List<ProductoRespuesta> listarDestacados() {

        return productoService.listarDestacados()
                .stream()
                .map(ProductoRespuesta::desde)
                .toList();

    }


    @GetMapping("/categoria/{categoriaId}")
    public List<ProductoRespuesta> listarPorCategoria(
            @PathVariable Long categoriaId) {

        return productoService.listarPorCategoria(categoriaId)
                .stream()
                .map(ProductoRespuesta::desde)
                .toList();

    }


    @GetMapping("/buscar")
    public List<ProductoRespuesta> buscarPorNombre(
            @RequestParam String nombre) {

        return productoService.buscarPorNombre(nombre)
                .stream()
                .map(ProductoRespuesta::desde)
                .toList();

    }


    @GetMapping("/{id}")
    public ProductoRespuesta buscarPorId(@PathVariable Long id) {

        Producto producto = productoService.buscarPorId(id);


        if (!producto.isActivo()) {

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Producto no disponible");

        }


        return ProductoRespuesta.desde(producto);

    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductoRespuesta crear(
            @RequestBody Producto producto,
            @RequestParam Long categoriaId) {

        return ProductoRespuesta.desde(
                productoService.crear(producto, categoriaId));

    }


    @PutMapping("/{id}")
    public ProductoRespuesta actualizar(
            @PathVariable Long id,
            @RequestBody Producto producto,
            @RequestParam Long categoriaId) {

        return ProductoRespuesta.desde(
                productoService.actualizar(
                        id, producto, categoriaId));

    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        productoService.eliminar(id);

    }


    @PatchMapping("/{id}/restaurar")
    public ProductoRespuesta restaurar(@PathVariable Long id) {

        return ProductoRespuesta.desde(
                productoService.restaurar(id));

    }


    public record ProductoRespuesta(
            Long id,

            String nombre,

            String descripcion,

            BigDecimal precio,

            int descuento,

            int stock,

            String imagen,

            String plataforma,

            String desarrollador,

            boolean destacado,

            boolean activo,

            Long categoriaId,

            String categoria
    ) {

        public static ProductoRespuesta desde(Producto producto) {

            return new ProductoRespuesta(
                    producto.getId(),
                    producto.getNombre(),
                    producto.getDescripcion(),
                    producto.getPrecio(),
                    producto.getDescuento(),
                    producto.getStock(),
                    producto.getImagen(),
                    producto.getPlataforma(),
                    producto.getDesarrollador(),
                    producto.isDestacado(),
                    producto.isActivo(),
                    producto.getCategoria().getId(),
                    producto.getCategoria().getNombre());

        }
    }
}