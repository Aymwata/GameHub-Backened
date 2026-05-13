package com.gamehub.productservice.client;

import com.gamehub.productservice.models.dto.CategoriaClientDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// El nombre identifica al servicio, la URL debe ser la del Category-Service local
@FeignClient(name = "category-service", url = "http://localhost:8081/api/categorias")
public interface CategoryClient {

    // 1. Endpoint para validar si la categoría existe ANTES de guardar un producto
    @GetMapping("/existe/{id}")
    boolean verificarExistencia(@PathVariable("id") Long id);

    // 2. Endpoint para traernos los datos de la categoría y armar el Response completo
    @GetMapping("/{id}")
    CategoriaClientDTO obtenerCategoriaPorId(@PathVariable("id") Long id);
}
