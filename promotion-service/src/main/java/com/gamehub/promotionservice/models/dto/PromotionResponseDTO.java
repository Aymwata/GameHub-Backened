package com.gamehub.promotionservice.models.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PromotionResponseDTO {

    private Long id;
    private String codigo;
    private String tipo;
    private Double valor;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private Double montoMinimo;
    private Integer usosMaximos;

    // ¡Aquí está el nuevo campo para que el frontend sepa cuántos usos lleva!
    private Integer usosActuales;

    private Boolean estado;

    // Los "espejos" con la información traída por Feign
    private ProductoClientDTO producto;
    private CategoriaClientDTO categoria;
}
