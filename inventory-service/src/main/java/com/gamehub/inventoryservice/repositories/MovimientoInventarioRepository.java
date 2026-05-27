// Interfaz de Spring Data JPA para guardar registros en la tabla de historial de movimientos.
package com.gamehub.inventoryservice.repositories;

import com.gamehub.inventoryservice.models.MovimientoInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Long> {

    List<MovimientoInventario> findByProductId(Long productId);
}
