package cl.fortinity.backend.repository;

import cl.fortinity.backend.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByActivoTrue();


    List<Producto> findByDestacadoTrueAndActivoTrue();


    List<Producto> findByCategoriaIdAndActivoTrue(Long categoriaId);


    List<Producto> findByNombreContainingIgnoreCaseAndActivoTrue(String nombre);
}