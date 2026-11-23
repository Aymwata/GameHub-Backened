package com.gamehub.productservice.models.dto;

import lombok.Data;
import java.util.Map;

@Data
public class ProductoResponseDTO {
    private Long id;
    private String nombre;
    private String marca;
    private String modelo;
    private String descripcion;
    private String imagen;
    private Map<String, String> especificaciones;
    private Double precio;
    private Boolean estado;

    private CategoriaClientDTO categoria;
}
