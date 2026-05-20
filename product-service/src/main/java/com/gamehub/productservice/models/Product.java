package com.gamehub.productservice.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "productos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String marca; // Añadido por rúbrica

    @Column(nullable = false)
    private String modelo; // Añadido por rúbrica

    private String descripcion;

    @Column(nullable = false)
    private Double precio;

    @Column(name = "categoria_id", nullable = false)
    private Long categoriaId;

    private Boolean estado = true;
}
