// Estructura de salida que muestra el pago con su estado APPROVED y código de transacción único.
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