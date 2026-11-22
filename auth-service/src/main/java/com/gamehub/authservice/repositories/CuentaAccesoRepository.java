package com.gamehub.authservice.repositories;
import com.gamehub.authservice.models.CuentaAcceso;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CuentaAccesoRepository extends JpaRepository<CuentaAcceso, Long> {
    Optional<CuentaAcceso> findByEmailIgnoreCase(String email);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select cuenta from CuentaAcceso cuenta where cuenta.id = :id")
    Optional<CuentaAcceso> bloquearPorId(@Param("id") Long id);
}
