package com.gamehub.promotionservice.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "promociones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Promotion extends Audit{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String codigo; // Ej: "GAMER2026"

    @Column(nullable = false)
    private String tipo; // Ej: "PORCENTAJE", "MONTO_FIJO"

    @Column(nullable = false)
    private Double valor; // Ej: 15.0 o 5000.0

    @Column(nullable = false)
    private LocalDateTime fechaInicio; // Cambiado a LocalDateTime para coincidir con los DTOs

    @Column(nullable = false)
    private LocalDateTime fechaFin; // Cambiado a LocalDateTime para coincidir con los DTOs

    private Double montoMinimo; // Monto mínimo de compra requerido

    @Column(nullable = false)
    private Integer usosMaximos; // Límite total de canjes permitidos

    @Column(nullable = false)
    private Integer usosActuales = 0; // Inicializado en 0 por defecto para que funcione tu @Query

    // ¡AQUÍ ESTÁ LA SOLUCIÓN! Las llaves foráneas lógicas para conectar con los otros microservicios
    private Long productoId;
    private Long categoriaId;

    @Column(nullable = false)
    private Boolean estado = true; // Vigente por defecto al crearse
}