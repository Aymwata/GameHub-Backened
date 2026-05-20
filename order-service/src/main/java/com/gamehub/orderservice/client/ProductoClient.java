package com.gamehub.orderservice.client;

import com.gamehub.orderservice.model.DTOs.ProductoClientDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service", url = "http://localhost:8082/api/productos") // Ajusta el puerto
public interface ProductoClient {
    @GetMapping("/{id}")
    ProductoClientDTO obtenerProducto(@PathVariable("id") Long id);
}