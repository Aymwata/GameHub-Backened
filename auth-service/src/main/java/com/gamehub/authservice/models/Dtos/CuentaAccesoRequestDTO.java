package com.gamehub.authservice.models.Dtos;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CuentaAccesoRequestDTO {

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Debe ser un formato de correo válido")
    private String email;

    @NotBlank(message = "La contraseña no puede estar vacía")
    private String password;

    @NotBlank(message = "El rol es obligatorio")
    private String rol;
}