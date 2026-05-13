package com.gamehub.authservice.Repositories;
import com.gamehub.authservice.Models.CuentaAcceso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CuentaAccesoRepository extends JpaRepository<CuentaAcceso, Long> {
    Optional<CuentaAcceso> findByEmail(String email);
}