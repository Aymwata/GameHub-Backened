package com.gamehub.userservice.models;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;


@Data
@Entity
@Table(name = "usuarios")
public class Usuario extends Audit{             //AL USAR HERENCIA YA UTILIZARA LA AUDITORIA

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @Column(unique = true, nullable = false)
    private String email;

    private String telefono;

    private String rol;

    private Boolean estado; // true = activo, false = inactivo

    // Relación bidireccional con direcciones
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Direccion> direcciones;



}
