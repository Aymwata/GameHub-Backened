package com.gamehub.orderservice.repository;

import com.gamehub.orderservice.model.Orden;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface OrdenRepository extends JpaRepository<Orden, Long> {
    List<Orden> findByUsuarioId(Long usuarioId);
    List<Orden> findByEstado(String estado);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select o from Orden o where o.id = :id")
    Optional<Orden> findByIdForUpdate(@Param("id") Long id);
}
