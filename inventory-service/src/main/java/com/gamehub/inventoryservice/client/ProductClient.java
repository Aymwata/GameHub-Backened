package com.gamehub.inventoryservice.client; // o services si no lo moviste

import com.gamehub.inventoryservice.models.dto.ProductClientDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service", url = "http://localhost:8083")
public interface ProductClient {

    @GetMapping("/api/products/{id}") // Ojo: tu compañero puso /api/v1/, asegúrate de que coincida con la ruta real de tu product-service
    ProductClientDTO getProductoById(@PathVariable("id") Long id);
}