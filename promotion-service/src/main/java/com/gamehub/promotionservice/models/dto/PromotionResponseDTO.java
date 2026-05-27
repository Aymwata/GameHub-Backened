// JSON limpio que se le devuelve al cliente mostrando el descuento ya aplicado y el nuevo total.
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


    private Integer usosActuales;

    private Boolean estado;


    private ProductoClientDTO producto;
    private CategoriaClientDTO categoria;
}
