package com.gamehub.orderservice.client;

import com.gamehub.orderservice.model.DTOs.InventarioClientDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "inventory-service", url = "http://localhost:8083/api/inventories") // Ajusta el puerto
public interface InventarioClient {

    @GetMapping("/product/{productId}")
    InventarioClientDTO consultarStock(@PathVariable("productId") Long productId);

    @PatchMapping("/reserve")
    void reservarStock(@RequestParam("productId") Long productId, @RequestParam("quantity") Integer quantity);
}
