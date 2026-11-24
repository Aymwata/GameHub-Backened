package com.gamehub.productservice.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;
import java.util.Map;


@Entity
@Table(name = "productos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Product extends Audit{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String marca;

    @Column(nullable = false)
    private String modelo;

    private String descripcion;

    private String imagen;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "producto_detalles_tecnicos", joinColumns = @JoinColumn(name = "producto_id"))
    @MapKeyColumn(name = "caracteristica", length = 80)
    @Column(name = "detalle", length = 500)
    private Map<String, String> detallesTecnicos = new LinkedHashMap<>();

    @Column(nullable = false)
    private Double precio;

    @Column(name = "categoria_id", nullable = false)
    private Long categoriaId;

    private Boolean estado = true;
}
