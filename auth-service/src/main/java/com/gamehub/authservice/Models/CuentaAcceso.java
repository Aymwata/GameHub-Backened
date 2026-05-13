package com.gamehub.authservice.Models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "cuentas_acceso")
public class CuentaAcceso extends Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password; // Guardado en texto plano por instrucción de no usar seguridad

    @Column(nullable = false)
    private String rol;

    private Boolean estado; // true = activo, false = inactivo
}