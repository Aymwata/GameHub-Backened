package com.gamehub.reviewservice.Repositories;

import com.gamehub.reviewservice.Models.Resena;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ResenaRepository extends JpaRepository<Resena, Long> {
    List<Resena> findByProductoId(Long productoId);
    List<Resena> findByUsuarioId(Long usuarioId);
    boolean existsByOrdenIdAndProductoId(Long ordenId, Long productoId);
}