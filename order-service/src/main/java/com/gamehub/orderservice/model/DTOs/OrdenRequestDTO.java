package com.gamehub.orderservice.model.DTOs;

import com.gamehub.orderservice.model.DatosCheckout;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class OrdenRequestDTO {

    @NotNull(message = "El ID del usuario es obligatorio")
    private Long usuarioId;

    private String codigoPromocion; // Opcional

    @Valid
    private DatosCheckout datosCheckout;

    @NotEmpty(message = "La orden debe tener al menos un detalle")
    @Valid
    private List<DetalleOrdenRequestDTO> detalles;
}
