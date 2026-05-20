package com.gamehub.inventoryservice.repositories;

import com.gamehub.inventoryservice.models.MovimientoInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Long> {
    // Permite buscar todo el historial de movimientos de un producto específico
    List<MovimientoInventario> findByProductId(Long productId);
}
