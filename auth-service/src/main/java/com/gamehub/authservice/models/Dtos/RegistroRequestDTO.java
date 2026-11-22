package com.gamehub.authservice.models.Dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistroRequestDTO(
        @NotBlank @Size(max = 100) String nombre,
        @NotBlank @Email @Size(max = 100) String email,
        @NotBlank @Size(max = 9) String telefono,
        @NotBlank @Size(min = 8, max = 255) String password
) {
}
