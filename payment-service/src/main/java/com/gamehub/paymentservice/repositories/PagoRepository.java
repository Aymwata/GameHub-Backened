// Conector JPA para guardar los pagos aprobados y validar duplicados usando "existsBy".
package com.gamehub.paymentservice.repositories;

import com.gamehub.paymentservice.models.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {
    List<Pago> findByOrdenId(Long ordenId);
    List<Pago> findByUsuarioId(Long usuarioId);
    List<Pago> findByEstado(String estado);
    boolean existsByOrdenIdAndEstado(Long ordenId, String estado);
}