package com.gamehub.inventoryservice.Models; // Models con M mayúscula

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "inventories")
@Data
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;
    private Integer stockAvailable;
    private Integer stockReserved;
    private String location;
    private Integer minStock;


    public Integer getActualAvailable() {
        return (stockAvailable != null ? stockAvailable : 0) - (stockReserved != null ? stockReserved : 0);
    }
}