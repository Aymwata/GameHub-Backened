package com.gamehub.authservice.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "cuentas_acceso")
public class CuentaAcceso extends Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    @JsonIgnore
    @ToString.Exclude
    private String password;

    @Column(name = "rol", nullable = false, length = 50)
    private String rol;

    @Column(name = "estado", nullable = false)
    private Boolean estado; // true = activo, false = inactivo

    @JsonIgnore
    private UUID registroId;

    @JsonIgnore
    private Long usuarioId;
}
