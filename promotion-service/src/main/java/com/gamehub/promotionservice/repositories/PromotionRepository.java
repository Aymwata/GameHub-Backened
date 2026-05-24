package com.gamehub.promotionservice.repositories;

import com.gamehub.promotionservice.models.Promotion; // Asegúrate de que el nombre coincida con tu clase
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {


    Optional<Promotion> findByCodigo(String codigo);

}