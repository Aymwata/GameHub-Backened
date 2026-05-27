// Entidad JPA mapeada directo a la tabla de la Base de Datos que guarda el stock actual.
package com.gamehub.inventoryservice.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "inventories")
@Data
public class Inventory extends Audit{
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