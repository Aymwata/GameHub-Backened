package com.gamehub.orderservice.model.DTOs;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class OrdenRequestDTO {

    @NotNull(message = "El ID del usuario es obligatorio")
    private Long usuarioId;

    private String codigoPromocion; // Opcional

    @NotEmpty(message = "La orden debe tener al menos un detalle")
    private List<DetalleOrdenRequestDTO> detalles;
}