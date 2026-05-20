package com.gamehub.orderservice.model.DTOs;

import lombok.Data;

@Data
public class UsuarioClientDTO {
    private Long id;
    private String nombre;
    private Boolean estado;
}