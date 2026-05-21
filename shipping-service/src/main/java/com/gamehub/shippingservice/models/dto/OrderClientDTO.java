package com.gamehub.shippingservice.models.dto;

import lombok.Data;

@Data
public class OrderClientDTO {
    private Long id;
    private Long usuarioId; // Coincide con el modelo sugerido de la rúbrica [cite: 173]
    private String estado; // Vital para verificar si dice "PAGADA"
}
