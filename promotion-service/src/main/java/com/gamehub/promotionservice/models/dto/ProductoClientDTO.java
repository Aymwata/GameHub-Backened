package com.gamehub.promotionservice.models.dto;

import lombok.Data;

@Data
public class ProductoClientDTO {
    private Long id;
    private String nombre;
    private Double precio; // Útil por si la promoción necesita validar el precio base
}
