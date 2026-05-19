package com.gamehub.inventoryservice.Services;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// Forzamos el puerto 8083 directamente
@FeignClient(name = "product-service", url = "http://localhost:8083")
public interface ProductClient {

    @GetMapping("/api/v1/productos/{id}")
    Object getProductoById(@PathVariable("id") Long id);

}