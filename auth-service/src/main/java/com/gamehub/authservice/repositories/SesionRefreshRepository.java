package com.gamehub.authservice.repositories;

import com.gamehub.authservice.models.SesionRefresh;
import java.time.Instant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SesionRefreshRepository extends JpaRepository<SesionRefresh, String> {
    @Modifying
    @Query("delete from SesionRefresh sesion where sesion.tokenHash = :hash")
    int consumir(@Param("hash") String hash);

    void deleteAllByCuentaId(Long cuentaId);

    void deleteAllByCuentaIdAndVenceBefore(Long cuentaId, Instant vence);
}
