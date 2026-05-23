package com.gamehub.userservice.models.Dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UsuarioRequestDTO {

    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(max = 100, message = "El nombre no puede exceder los 100 caracteres")
    private String nombre;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato del email no es válido")
    @Size(max = 100, message = "El email no puede exceder los 100 caracteres")
    private String email;

    @NotBlank(message = "El teléfono es obligatorio")
    @Size(max = 9, message = "El teléfono no puede exceder los 9 caracteres")
    private String telefono;

    @NotBlank(message = "El rol es obligatorio (EJ: ADMIN, CLIENTE, OPERADOR)")
    @Size(max = 50, message = "El rol no puede exceder los 50 caracteres")
    private String rol;
}
