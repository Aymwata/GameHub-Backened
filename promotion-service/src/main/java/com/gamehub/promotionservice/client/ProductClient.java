package com.gamehub.promotionservice.client;

import com.gamehub.promotionservice.models.dto.ProductoClientDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service", url = "http://localhost:8083/api/products")
public interface ProductClient {

    // Asumiendo que tu product-service tiene el metodo estandar GET /{id}
    @GetMapping("/{id}")
    ProductoClientDTO obtenerProducto(@PathVariable("id") Long id);
}