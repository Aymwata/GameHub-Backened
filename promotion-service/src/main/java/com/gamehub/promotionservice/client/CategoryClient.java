package com.gamehub.promotionservice.client;

import com.gamehub.promotionservice.models.dto.CategoriaClientDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "category-service", url = "http://localhost:8081/api/categories")
public interface CategoryClient {


    @GetMapping("/{id}")
    CategoriaClientDTO obtenerCategoria(@PathVariable("id") Long id);
}