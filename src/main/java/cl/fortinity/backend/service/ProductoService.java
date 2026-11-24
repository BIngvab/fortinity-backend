package cl.fortinity.backend.service;

import cl.fortinity.backend.model.Producto;
import cl.fortinity.backend.model.Categoria;
import cl.fortinity.backend.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.util.List;


@Service
@Transactional
public class ProductoService {

    private final ProductoRepository productoRepository;


    private final CategoriaService categoriaService;


    public ProductoService(
            ProductoRepository productoRepository,
            CategoriaService categoriaService) {

        this.productoRepository = productoRepository;
        this.categoriaService = categoriaService;

    }


    public List<Producto> listar() {
        return productoRepository.findByActivoTrue();

    }


    public List<Producto> listarTodos() {
        return productoRepository.findAll();

    }


    public List<Producto> listarDestacados() {
        return productoRepository.findByDestacadoTrueAndActivoTrue();

    }


    public List<Producto> listarPorCategoria(Long categoriaId) {
        return productoRepository.findByCategoriaIdAndActivoTrue(categoriaId);

    }


    public List<Producto> buscarPorNombre(String nombre) {
        return productoRepository
                .findByNombreContainingIgnoreCaseAndActivoTrue(nombre);

    }


    public Producto buscarPorId(Long id) {

        return productoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Producto no encontrado"));

    }


    public Producto crear(Producto datos, Long categoriaId) {

        validarProducto(datos);

        
        Categoria categoria = categoriaService.buscarPorId(categoriaId);


        Producto producto = new Producto();


        copiarDatos(producto, datos);


        producto.setCategoria(categoria);


        return productoRepository.save(producto);

    }


    public Producto actualizar(
            Long id, Producto datos, Long categoriaId) {

        Producto producto = buscarPorId(id);


        validarProducto(datos);


        Categoria categoria = categoriaService.buscarPorId(categoriaId);


        copiarDatos(producto, datos);


        producto.setCategoria(categoria);

        
        return productoRepository.save(producto);

    }


    public void eliminar(Long id) {

        Producto producto = buscarPorId(id);


        producto.setActivo(false);


        productoRepository.save(producto);

    }


    public Producto restaurar(Long id) {

        Producto producto = buscarPorId(id);


        producto.setActivo(true);


        return productoRepository.save(producto);

    }


    private void copiarDatos(Producto producto, Producto datos) {

        producto.setNombre(datos.getNombre().trim());
        producto.setDescripcion(datos.getDescripcion());
        producto.setPrecio(datos.getPrecio());
        producto.setDescuento(datos.getDescuento());
        producto.setStock(datos.getStock());
        producto.setImagen(datos.getImagen());
        producto.setPlataforma(datos.getPlataforma());
        producto.setDesarrollador(datos.getDesarrollador());
        producto.setDestacado(datos.isDestacado());

    }


    private void validarProducto(Producto producto) {

        if (producto.getNombre() == null
                || producto.getNombre().isBlank()
                || producto.getNombre().trim().length() > 150) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Nombre de producto invalido");

        }


        if (producto.getPrecio() == null
                || producto.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El precio debe ser mayor a cero");

        }


        if (producto.getDescuento() < 0
                || producto.getDescuento() > 100) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El descuento debe estar entre 0 y 100");

        }


        if (producto.getStock() < 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El stock no puede ser negativo");

        }
    }
}