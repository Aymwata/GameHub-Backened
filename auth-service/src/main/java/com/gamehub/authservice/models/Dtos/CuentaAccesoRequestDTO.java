package com.gamehub.authservice.models.Dtos;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CuentaAccesoRequestDTO {

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Debe ser un formato de correo válido")
    @Size(max = 100)
    private String email;

    @NotBlank(message = "La contraseña no puede estar vacía")
    @Size(min = 8, max = 255)
    private String password;

    @NotBlank(message = "El rol es obligatorio")
    @Size(max = 50)
    private String rol;
}
