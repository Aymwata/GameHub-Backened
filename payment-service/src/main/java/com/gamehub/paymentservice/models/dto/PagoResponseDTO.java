package com.gamehub.paymentservice.models.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PagoResponseDTO {
    private Long id;
    private Long ordenId;
    private Double monto;
    private String metodo;
    private String estado;
    private String codigoTransaccion;
    private Long usuarioId;
    private LocalDateTime createdAt;
}