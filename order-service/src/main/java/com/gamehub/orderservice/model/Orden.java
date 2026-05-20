package com.gamehub.orderservice.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;


@Entity
@Table(name = "ordenes")
@Data
public class Orden {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long usuarioId; // Referencia al user-service

    @Column(nullable = false)
    private String estado; // PENDIENTE, PAGADA, CANCELADA

    private Double subtotal = 0.0;
    private Double descuento = 0.0;
    private Double total = 0.0;

    private String codigoPromocion;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DetalleOrden> detalles;
    
}
