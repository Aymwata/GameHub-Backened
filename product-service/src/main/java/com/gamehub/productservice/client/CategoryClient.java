package com.gamehub.productservice.client;

import com.gamehub.productservice.models.dto.CategoriaClientDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "category-service", url = "http://localhost:8081/api/categories")
public interface CategoryClient {

    @GetMapping("/existe/{id}")
    boolean verificarExistencia(@PathVariable("id") Long id);

    @GetMapping("/{id}")
    CategoriaClientDTO obtenerCategoriaPorId(@PathVariable("id") Long id);
}