package com.gamehub.userservice.repositories;

import com.gamehub.userservice.models.Direccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DireccionRepository extends JpaRepository<Direccion, Long> {
    // Permite obtener todas las direcciones de un usuario específico
    List<Direccion> findByUsuarioId(Long usuarioId);
}
