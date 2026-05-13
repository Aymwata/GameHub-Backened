package com.gamehub.userservice.Repositories;

import com.gamehub.userservice.Models.Direccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DireccionRepository extends JpaRepository<Direccion, Long> {
    // Permite obtener todas las direcciones de un usuario específico
    List<Direccion> findByUsuarioId(Long usuarioId);
}
