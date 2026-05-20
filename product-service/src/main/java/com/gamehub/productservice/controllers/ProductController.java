package com.gamehub.productservice.controllers;

import com.gamehub.productservice.models.dto.ProductoRequestDTO;
import com.gamehub.productservice.models.dto.ProductoResponseDTO;
import com.gamehub.productservice.services.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products") // Ruta base para productos
@RequiredArgsConstructor
public class ProductController {

    private final ProductService service;

    @PostMapping
    public ResponseEntity<ProductoResponseDTO> crearProducto(@Valid @RequestBody ProductoRequestDTO request) {
        // Al llamar al service aquí, internamente Feign consultará al category-service
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crearProducto(request));
    }

    @GetMapping
    public ResponseEntity<List<ProductoResponseDTO>> obtenerTodos() {
        return ResponseEntity.ok(service.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> obtenerProducto(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }
}
