package com.gamehub.orderservice.client;

import com.gamehub.orderservice.model.DTOs.InventarioClientDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "inventory-service", url = "http://localhost:8085/api/inventory") // Ajusta el puerto
public interface InventarioClient {

    @GetMapping("/product/{productId}")
    InventarioClientDTO consultarStock(@PathVariable("productId") Long productId);

    // Cambiamos de @PatchMapping a @PutMapping
    @PutMapping("/reserve")
    void reservarStock(@RequestParam("productId") Long productId, @RequestParam("quantity") Integer quantity);
}
