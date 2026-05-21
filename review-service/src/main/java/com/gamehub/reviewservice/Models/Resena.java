package com.gamehub.reviewservice.Models;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "resenas")
@Data
public class Resena {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long usuarioId;

    @Column(nullable = false)
    private Long productoId;

    @Column(nullable = false)
    private Long ordenId;

    @Column(nullable = false)
    private Integer puntuacion;

    @Column(length = 500)
    private String comentario;

    @Column(nullable = false)
    private String estado; // ACTIVA, MODERADA, ELIMINADA

    @Column(nullable = false)
    private LocalDateTime fecha = LocalDateTime.now();
}