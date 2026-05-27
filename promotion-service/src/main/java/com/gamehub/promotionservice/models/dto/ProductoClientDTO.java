// Estructura para recibir los datos de un producto desde product-service y verificar si tiene una rebaja activa.
package com.gamehub.promotionservice.models.dto;

import lombok.Data;

@Data
public class ProductoClientDTO {
    private Long id;
    private String nombre;
    private Double precio;
}
