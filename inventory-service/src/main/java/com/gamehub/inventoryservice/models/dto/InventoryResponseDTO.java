package com.gamehub.inventoryservice.models.dto;

import lombok.Data;

@Data
public class InventoryResponseDTO {
    private Long id;
    private Long productId;
    private Integer stockAvailable;
    private Integer stockReserved;
    private Integer actualAvailable; // El cálculo limpio
    private String location;
}
