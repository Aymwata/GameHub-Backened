package com.gamehub.orderservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;


@Entity
@Table(name = "ordenes")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Orden extends Audit{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long usuarioId; // Referencia al user-service

    @Column(length = 100)
    private String nombreContacto;
    @Column(length = 100)
    private String correoContacto;
    @Column(length = 20)
    private String telefonoContacto;
    @Column(length = 100)
    private String region;
    @Column(length = 100)
    private String ciudad;
    @Column(length = 100)
    private String comuna;
    @Column(length = 150)
    private String calle;
    @Column(length = 20)
    private String numero;

    @Column(nullable = false)
    private String estado; // PENDIENTE, PAGADA, CANCELADA

    private Double subtotal = 0.0;
    private Double descuento = 0.0;
    private Double total = 0.0;

    private String codigoPromocion;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<DetalleOrden> detalles;
    
}

