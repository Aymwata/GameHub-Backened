// Molde adaptado que lee el JSON del servicio de órdenes para validar montos e IDs.
package com.gamehub.paymentservice.models.dto;

import lombok.Data;

@Data
public class OrdenClientDTO {
    private Long id;
    private Long usuarioId;
    private String estado;
    private Double total;
}