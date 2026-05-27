// Interfaz de Spring Data JPA para hacer el CRUD (guardar, buscar, borrar) del stock en BD.
package com.gamehub.inventoryservice.repositories;

import com.gamehub.inventoryservice.models.Inventory; // Import con M mayúscula
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByProductId(Long productId);
}