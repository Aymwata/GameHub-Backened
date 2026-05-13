package com.gamehub.userservice.Models;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@Entity
@Table(name = "direcciones")
public class Direccion extends Audit{             //AL USAR HERENCIA YA UTILIZARA LA AUDITORIA

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String comuna;
    private String ciudad;
    private String calle;
    private String numero;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    @JsonIgnore // Para evitar ciclos infinitos al serializar JSON
    private Usuario usuario;
}
