package com.gamehub.authservice.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "sesiones_refresh")
public class SesionRefresh {
    @Id
    @Column(length = 64)
    private String tokenHash;

    @Column(nullable = false)
    private Long cuentaId;

    @Column(nullable = false)
    private Instant vence;

    public SesionRefresh(String tokenHash, Long cuentaId, Instant vence) {
        this.tokenHash = tokenHash;
        this.cuentaId = cuentaId;
        this.vence = vence;
    }
}
