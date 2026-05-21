package com.gamehub.orderservice.model.DTOs;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PromocionClientDTO {
    private Long id;
    private String tipo; // PORCENTAJE, MONTO_FIJO
    private Double valor;
    private Double montoMinimo;
    private Boolean estado;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
}