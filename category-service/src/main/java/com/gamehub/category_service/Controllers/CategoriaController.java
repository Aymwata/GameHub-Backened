package com.gamehub.category_service.Controllers;

import com.gamehub.category_service.Models.Categoria;
import com.gamehub.category_service.Services.CategoriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias") // Ruta semántica base
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService service;

    @GetMapping
    public ResponseEntity<List<Categoria>> listarCategorias() {
        return ResponseEntity.ok(service.obtenerTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Categoria> obtenerCategoria(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<Categoria> crearCategoria(@Valid @RequestBody Categoria categoria) {
        Categoria nuevaCategoria = service.crearCategoria(categoria);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaCategoria);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Categoria> actualizarCategoria(
            @PathVariable Long id,
            @Valid @RequestBody Categoria categoria) {
        return ResponseEntity.ok(service.actualizarCategoria(id, categoria));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivarCategoria(@PathVariable Long id) {
        service.desactivarCategoria(id);
        return ResponseEntity.noContent().build();
    }
}