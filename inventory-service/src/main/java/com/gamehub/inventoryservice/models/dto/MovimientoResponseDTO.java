package com.gamehub.inventoryservice.models.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MovimientoResponseDTO {
    private Long id;
    private Long productId;
    private String tipo;
    private Integer cantidad;
    private LocalDateTime fechaMovimiento; // Aquí mapearemos el createdAt de la clase Audit
}