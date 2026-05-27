package com.gamehub.userservice.models.Dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DireccionRequestDTO {

    @NotNull(message = "El ID de usuario es obligatorio")
    private Long usuarioId;

    @NotBlank(message = "La comuna es obligatoria")
    @Size(max = 100, message = "La comuna no puede exceder los 100 caracteres")
    private String comuna;

    @NotBlank(message = "La ciudad es obligatoria")
    @Size(max = 100, message = "La ciudad no puede exceder los 100 caracteres")
    private String ciudad;

    @NotBlank(message = "La calle es obligatoria")
    @Size(max = 150, message = "La calle no puede exceder los 150 caracteres")
    private String calle;

    @NotBlank(message = "El número es obligatorio")
    @Size(max = 20, message = "La numeracion de su casa no puede exceder los 20 caracteres")
    private String numero;
}

