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

    // Tipos de movimiento: "INGRESO", "RESERVA", "LIBERACION", "VENTA_CONFIRMADA"
    @Column(nullable = false)
    private String tipo;

    @Column(nullable = false)
    private Integer cantidad;

    // La fecha ya está cubierta por tu clase Audit (createdAt)
}
