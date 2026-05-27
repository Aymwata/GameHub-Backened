// Valida los datos de entrada cuando un cliente digita un código de descuento en la tienda.
package com.gamehub.promotionservice.models.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PromotionRequestDTO {

    @NotBlank(message = "El código es obligatorio")
    private String codigo;

    @NotBlank(message = "El tipo es obligatorio (PORCENTAJE o MONTO_FIJO)")
    private String tipo;

    @NotNull(message = "El valor es obligatorio")
    @Positive(message = "El valor debe ser positivo")
    private Double valor;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDateTime fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDateTime fechaFin;

    @PositiveOrZero(message = "El monto mínimo no puede ser negativo")
    private Double montoMinimo;

    @NotNull(message = "Los usos máximos son obligatorios")
    @Min(value = 1, message = "Debe tener al menos 1 uso")
    private Integer usosMaximos;


    private Long productoId;
    private Long categoriaId;
}
