package com.gamehub.orderservice.model.DTOs;

import lombok.Data;

@Data
public class InventarioClientDTO {
    private Long productId;
    private Integer actualAvailable;
}