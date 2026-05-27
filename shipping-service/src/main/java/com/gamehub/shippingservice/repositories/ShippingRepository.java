package com.gamehub.shippingservice.repositories;

import com.gamehub.shippingservice.models.Shipping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShippingRepository extends JpaRepository<Shipping, Long> {


    Optional<Shipping> findByOrderId(Long orderId);


    List<Shipping> findByStatus(String status);


    Optional<Shipping> findByTrackingNumber(String trackingNumber);
}
