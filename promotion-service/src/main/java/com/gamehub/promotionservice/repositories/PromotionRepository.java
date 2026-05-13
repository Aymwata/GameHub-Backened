package com.gamehub.promotionservice.repositories;

import com.gamehub.promotionservice.models.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {


    Optional<Promotion> findByCodigo(String codigo);


    List<Promotion> findByEstado(Boolean estado);

    @Query("SELECT p FROM Promotion p WHERE p.estado = true AND p.usosActuales < p.usosMaximos")
    List<Promotion> findAvailablePromotions();
}