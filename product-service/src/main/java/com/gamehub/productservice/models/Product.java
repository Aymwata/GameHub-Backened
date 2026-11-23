package com.gamehub.productservice.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
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

    @Column(length = 2000)
    private String descripcion;

    @Column(length = 600)
    private String imagen;

    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, String> especificaciones;

    @Column(nullable = false)
    private Double precio;

    @Column(name = "categoria_id", nullable = false)
    private Long categoriaId;

    private Boolean estado = true;
}
