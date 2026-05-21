package com.gamehub.reviewservice.Client;

import com.gamehub.reviewservice.Models.DTOs.ProductoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service", url = "http://localhost:8083/api/products")
public interface ProductClient {
    @GetMapping("/{id}")
    ProductoDTO obtenerProducto(@PathVariable("id") Long id);
}