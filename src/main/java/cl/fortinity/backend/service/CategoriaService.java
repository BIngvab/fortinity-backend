package cl.fortinity.backend.service;

import cl.fortinity.backend.model.Categoria;
import cl.fortinity.backend.repository.CategoriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;


@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;


    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;

    }


    public List<Categoria> listar() {
        return categoriaRepository.findAll();

    }


    public Categoria buscarPorId(Long id) {

        return categoriaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Categoria no encontrada"));

    }


    public Categoria crear(Categoria categoria) {

        validarNombre(categoria.getNombre());


        String nombre = categoria.getNombre().trim();


        if (categoriaRepository.existsByNombreIgnoreCase(nombre)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "La categoria ya existe");

        }


        Categoria nueva = new Categoria();


        nueva.setNombre(nombre);
        nueva.setDescripcion(categoria.getDescripcion());


        return categoriaRepository.save(nueva);
    }


    public Categoria actualizar(Long id, Categoria datos) {

        Categoria categoria = buscarPorId(id);


        validarNombre(datos.getNombre());


        String nombre = datos.getNombre().trim();


        if (!categoria.getNombre().equalsIgnoreCase(nombre)
                && categoriaRepository.existsByNombreIgnoreCase(nombre)) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "La categoria ya existe");

        }


        categoria.setNombre(nombre);
        categoria.setDescripcion(datos.getDescripcion());


        return categoriaRepository.save(categoria);
    }


    public void eliminar(Long id) {

        Categoria categoria = buscarPorId(id);


        try {

            categoriaRepository.delete(categoria);
            categoriaRepository.flush();

        } catch (org.springframework.dao.DataIntegrityViolationException e) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede eliminar una categoria con productos asociados");

        }
    }


    private void validarNombre(String nombre) {

        if (nombre == null || nombre.isBlank()
                || nombre.trim().length() > 100) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Nombre de categoria invalido");

        }
    }
}