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
    @Column(name = "id")
    private Long id;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;

    @Column(name = "telefono", length = 20)
    private String telefono;

    @Column(name = "rol", nullable = false, length = 50)
    private String rol;

    @Column(name = "estado", nullable = false)
    private Boolean estado; // true = activo, false = inactivo

    // Relación bidireccional con direcciones
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Direccion> direcciones;

}
