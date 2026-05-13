package com.gamehub.promotionservice.models;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data // Genera Getters, Setters, toString, equals y hashCode automáticamente
@NoArgsConstructor // Constructor vacío necesario para frameworks como Jackson
@AllArgsConstructor // Constructor con todos los campos
public class PromotionDTO {

    @NotBlank(message = "El código de promoción no puede estar vacío")
    @Size(min = 3, max = 20, message = "El código debe tener entre 3 y 20 caracteres")
    private String codigo;

    @NotBlank(message = "El tipo de promoción (PORCENTAJE/MONTO) es obligatorio")
    private String tipo;

    @NotNull(message = "El valor del descuento es obligatorio")
    @Positive(message = "El valor debe ser un número positivo")
    private Double valor;

    @NotNull(message = "La fecha de inicio de la promoción es requerida")
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de finalización es requerida")
    @Future(message = "La fecha de fin debe ser una fecha futura")
    private LocalDate fechaFin;

    @Min(value = 0, message = "El monto mínimo de compra no puede ser negativo")
    private Double montoMinimo;

    @NotNull(message = "Debe definir un límite máximo de usos")
    @Min(value = 1, message = "Los usos máximos deben ser al menos 1")
    private Integer usosMaximos;
}