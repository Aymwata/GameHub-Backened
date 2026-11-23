package com.gamehub.productservice.models.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.Map;

@Data
public class ProductoRequestDTO {
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "La marca es obligatoria")
    private String marca;

    @NotBlank(message = "El modelo es obligatorio")
    private String modelo;

    @Size(max = 2000)
    private String descripcion;

    @Size(max = 600)
    private String imagen;

    @Size(max = 40)
    @JsonAlias("detallesTecnicos")
    private Map<@NotBlank @Size(max = 80) String, @NotBlank @Size(max = 500) String> especificaciones;

    @NotNull(message = "El precio es obligatorio")
    @Min(value = 0, message = "El precio no puede ser negativo")
    private Double precio;

    @NotNull(message = "El ID de la categoría es obligatorio")
    private Long categoriaId;
}
