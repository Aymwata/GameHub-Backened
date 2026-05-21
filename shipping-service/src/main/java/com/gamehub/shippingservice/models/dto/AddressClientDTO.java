package com.gamehub.shippingservice.models.dto;

import lombok.Data;

@Data
public class AddressClientDTO {
    private Long id;
    private Long usuarioId;
    private String calle;
    private String numero;
    private String comuna;
    private String ciudad;
    // Basado estrictamente en el modelo de Direccion sugerido [cite: 99]
}
