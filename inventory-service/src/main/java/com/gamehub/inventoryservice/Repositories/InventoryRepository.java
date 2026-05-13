package com.gamehub.inventoryservice.Repositories; // Repositories con R mayúscula

import com.gamehub.inventoryservice.Models.Inventory; // Import con M mayúscula
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByProductId(Long productId);
}