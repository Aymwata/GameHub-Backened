package com.gamehub.inventoryservice.Services;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.Optional;


@FeignClient(name = "product-service", url = "${product.service.url:http://localhost:8083}")
public interface ProductClient {


    @GetMapping("/api/productos/{id}")
    Object getProductoById(@PathVariable("id") Long id);

    // Nota académica: En una fase avanzada, deberías reemplazar 'Object'
    // por un 'ProductResponseDTO' para evitar el casting manual.
}