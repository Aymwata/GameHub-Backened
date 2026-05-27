// Entidad JPA que representa la tabla de historial (ingresos/egresos) en la base de datos.
package com.gamehub.inventoryservice.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "movimientos_inventario")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoInventario extends Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long productId;


    @Column(nullable = false)
    private String tipo;

    @Column(nullable = false)
    private Integer cantidad;


}
