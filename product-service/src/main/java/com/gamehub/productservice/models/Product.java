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

    private String descripcion;

    @Column(nullable = false)
    private Double precio;

    @Column(nullable = false)
    private Integer stock;

    // ¡ATENCIÓN A ESTO PARA TU DEFENSA!
    // No usamos @ManyToOne porque la Categoría está en OTRA base de datos.
    // Solo guardamos el ID como referencia.
    @Column(name = "categoria_id", nullable = false)
    private Long categoriaId;

    private Boolean estado = true;
}
