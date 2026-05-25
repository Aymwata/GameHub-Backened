package com.gamehub.category_service.controllers;

import com.gamehub.category_service.models.dto.CategoriaRequestDTO;
import com.gamehub.category_service.models.dto.CategoriaResponseDTO;
import com.gamehub.category_service.services.CategoriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService service;

    @GetMapping
    public ResponseEntity<List<CategoriaResponseDTO>> listarCategorias() {
        return ResponseEntity.ok(service.obtenerTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> obtenerCategoria(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<CategoriaResponseDTO> crearCategoria(@Valid @RequestBody CategoriaRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crearCategoria(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> actualizarCategoria(
            @PathVariable Long id,
            @Valid @RequestBody CategoriaRequestDTO request) {
        return ResponseEntity.ok(service.actualizarCategoria(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivarCategoria(@PathVariable Long id) {
        service.desactivarCategoria(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/existe/{id}")
    public ResponseEntity<Boolean> verificarExistencia(@PathVariable Long id) {
        return ResponseEntity.ok(service.existeCategoria(id));
    }
}