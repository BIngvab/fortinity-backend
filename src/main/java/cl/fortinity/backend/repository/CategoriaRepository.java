package cl.fortinity.backend.repository;

import cl.fortinity.backend.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    Optional<Categoria> findByNombre(String nombre);


    boolean existsByNombreIgnoreCase(String nombre);
}