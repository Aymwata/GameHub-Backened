package com.gamehub.shippingservice.repositories;

import com.gamehub.shippingservice.models.Shipping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShippingRepository extends JpaRepository<Shipping, Long> {

    // Para cumplir con "Listar despachos por orden"
    Optional<Shipping> findByOrderId(Long orderId);

    // Para cumplir con "Listar despachos por estado"
    List<Shipping> findByStatus(String status);

    // Validar tracking único antes de actualizar
    Optional<Shipping> findByTrackingNumber(String trackingNumber);
}
