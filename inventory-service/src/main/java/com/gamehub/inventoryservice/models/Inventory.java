// Entidad JPA mapeada directo a la tabla de la Base de Datos que guarda el stock actual.
package com.gamehub.inventoryservice.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "inventories")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor

public class Inventory extends Audit{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private Integer stockAvailable;

    @Column(nullable = false)
    private Integer stockReserved;

    private String location;

    private Integer minStock;

    // CALCULO DE INVENTARIO
    // Retorna el stock real que se puede vender restando las reservas al total disponible
    public Integer getActualAvailable() {
        return (stockAvailable != null ? stockAvailable : 0) - (stockReserved != null ? stockReserved : 0);
    }
}