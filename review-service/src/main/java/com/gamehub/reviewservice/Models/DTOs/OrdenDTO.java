package com.gamehub.reviewservice.Models.DTOs;

import lombok.Data;

import java.util.List;

@Data
public class OrdenDTO {
    private Long id;
    private Long usuarioId;
    private String estado;
    private List<DetalleOrdenDTO> detalles;
}