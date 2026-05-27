// Entidad JPA que crea y gestiona la tabla de pagos en tu base de datos local.
package com.gamehub.paymentservice.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pagos")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pago extends Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long ordenId;

    @Column(nullable = false)
    private Double monto;

    @Column(nullable = false)
    private String metodo;

    @Column(nullable = false)
    private String estado;

    @Column(unique = true)
    private String codigoTransaccion;

    @Column(nullable = false)
    private Long usuarioId;
}