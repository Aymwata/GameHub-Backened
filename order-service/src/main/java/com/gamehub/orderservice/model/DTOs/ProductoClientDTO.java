package com.gamehub.orderservice.model.DTOs;

import lombok.Data;

@Data
public class ProductoClientDTO {
    private Long id;
    private String nombre;
    private Double precio;
    private Boolean estado;
}