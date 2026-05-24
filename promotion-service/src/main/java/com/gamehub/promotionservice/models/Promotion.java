package com.gamehub.promotionservice.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "promociones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String codigo; // Ej: "GAMER2026"

    @Column(nullable = false)
    private String tipo;

    @Column(nullable = false)
    private Double valor;
    @Column(nullable = false)
    private LocalDateTime fechaInicio;

    @Column(nullable = false)
    private LocalDateTime fechaFin;

    private Double montoMinimo;

    @Column(nullable = false)
    private Integer usosMaximos;

    @Column(nullable = false)
    private Integer usosActuales = 0;


    private Long productoId;
    private Long categoriaId;

    @Column(nullable = false)
    private Boolean estado = true;
}