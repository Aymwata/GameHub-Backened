// Estructura temporal para recibir y procesar los datos JSON que envía el servicio de productos.
package com.gamehub.inventoryservice.models.dto;

import lombok.Data;

@Data
public class ProductClientDTO {
    private Long id;
    private String nombre;
}
