package com.gamehub.paymentservice.models.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class PagoRequestDTO {

    @NotNull(message = "El ID de la orden es obligatorio.")
    private Long ordenId;

    @NotNull(message = "El monto es obligatorio.")
    @Positive(message = "El monto debe ser un valor mayor a cero.")
    private Double monto;

    @NotBlank(message = "El método de pago es obligatorio.")
    private String metodo;
}