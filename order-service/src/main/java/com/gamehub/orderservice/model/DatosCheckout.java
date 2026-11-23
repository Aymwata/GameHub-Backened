package com.gamehub.orderservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class DatosCheckout {
    @NotBlank @Size(max = 100)
    @Column(name = "destinatario_nombre", length = 100)
    private String nombre;

    @NotBlank @Email @Size(max = 100)
    @Column(name = "destinatario_email", length = 100)
    private String email;

    @NotBlank @Pattern(regexp = "[0-9]{9}")
    @Column(name = "destinatario_telefono", length = 9)
    private String telefono;

    @NotBlank @Size(max = 100)
    @Column(name = "despacho_region", length = 100)
    private String region;

    @NotBlank @Size(max = 100)
    @Column(name = "despacho_comuna", length = 100)
    private String comuna;

    @NotBlank @Size(max = 200)
    @Column(name = "despacho_direccion", length = 200)
    private String direccion;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago_preferido", length = 20)
    private MetodoPago metodoPago;

    public enum MetodoPago { TARJETA, TRANSFERENCIA }
}
