package com.gamehub.promotionservice.models;


import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "promociones")
@Data
public class Promotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String codigo; // Ej: "GAMER10"

    private String tipo; // "PORCENTAJE" o "FIJO"
    private Double valor;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Double montoMinimo;
    private Integer usosMaximos;
    private Integer usosActuales; // Para controlar la regla de usos máximos
    private Boolean estado; // true = vigente, false = desactivada

}