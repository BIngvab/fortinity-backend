package cl.fortinity.backend.repository;

import cl.fortinity.backend.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;


public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByActivoTrue();


    List<Producto> findByDestacadoTrueAndActivoTrue();


    List<Producto> findByCategoriaIdAndActivoTrue(Long categoriaId);


    List<Producto> findByNombreContainingIgnoreCaseAndActivoTrue(String nombre);


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Producto p WHERE p.id = :id")
    Optional<Producto> buscarParaCompra(@Param("id") Long id);
}