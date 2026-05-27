package com.gamehub.productservice.models.dto;

import lombok.Data;

@Data
public class ProductoResponseDTO {
    private Long id;
    private String nombre;
    private String marca;
    private String modelo;
    private String descripcion;
    private Double precio;
    private Boolean estado;

    private CategoriaClientDTO categoria;
}
