package com.gamehub.promotionservice.controllers;

import com.gamehub.promotionservice.models.dto.CategoriaClientDTO;
import com.gamehub.promotionservice.models.dto.ProductoClientDTO;
import com.gamehub.promotionservice.services.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/promotions")
@RequiredArgsConstructor
public class PromotionController {

    private final PromotionService service;

    // Endpoint para probar conexión con el puerto 8081
    @GetMapping("/test/categoria/{id}")
    public ResponseEntity<CategoriaClientDTO> testConexionCategoria(@PathVariable Long id) {
        return ResponseEntity.ok(service.probarConexionCategoria(id));
    }

    // Endpoint para probar conexión con el puerto 8082
    @GetMapping("/test/producto/{id}")
    public ResponseEntity<ProductoClientDTO> testConexionProducto(@PathVariable Long id) {
        return ResponseEntity.ok(service.probarConexionProducto(id));
    }
}