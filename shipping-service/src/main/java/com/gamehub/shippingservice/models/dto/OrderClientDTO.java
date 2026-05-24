package com.gamehub.shippingservice.models.dto;

import lombok.Data;

@Data
public class OrderClientDTO {
    private Long id;
    private Long usuarioId;
    private String estado;
}
