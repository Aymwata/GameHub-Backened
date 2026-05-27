// Molde temporal para leer las categorías remotas y validar si el cupón aplica a un tipo de juego específico.
package com.gamehub.promotionservice.models.dto;

import lombok.Data;

@Data
public class CategoriaClientDTO {
    private Long id;
    private String nombre;
}
