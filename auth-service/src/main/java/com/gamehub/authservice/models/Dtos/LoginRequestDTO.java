package com.gamehub.authservice.models.Dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequestDTO {
    @NotBlank(message = "El email es obligatorio")
    @Email
    @Size(max = 100)
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(max = 255)
    private String password;

    @NotNull(message = "El perfil debe ser CLIENTE o ADMIN")
    private Perfil perfil;

    public enum Perfil {
        CLIENTE, ADMIN
    }
}

