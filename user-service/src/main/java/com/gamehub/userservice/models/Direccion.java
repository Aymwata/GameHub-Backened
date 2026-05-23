package com.gamehub.userservice.models;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@Entity
@Table(name = "direcciones")
public class Direccion extends Audit{             //AL USAR HERENCIA YA UTILIZARA LA AUDITORIA

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "comuna", nullable = false, length = 100)
    private String comuna;

    @Column(name = "ciudad", nullable = false, length = 100)
    private String ciudad;

    @Column(name = "calle", nullable = false, length = 150)
    private String calle;

    @Column(name = "numero", nullable = false, length = 20)
    private String numero;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    @JsonIgnore // Para evitar ciclos infinitos al serializar JSON
    private Usuario usuario;
}
