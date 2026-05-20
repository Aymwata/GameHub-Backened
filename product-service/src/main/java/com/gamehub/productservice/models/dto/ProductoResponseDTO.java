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

    // En lugar de devolver solo un número (categoriaId),
    // devolvemos el objeto espejo con los datos reales de la categoría.
    private CategoriaClientDTO categoria;
}
