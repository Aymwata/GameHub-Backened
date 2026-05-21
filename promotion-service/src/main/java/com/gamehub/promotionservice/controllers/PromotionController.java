package com.gamehub.promotionservice.controllers;

import com.gamehub.promotionservice.models.dto.PromotionRequestDTO;
import com.gamehub.promotionservice.models.dto.PromotionResponseDTO;
import com.gamehub.promotionservice.services.PromotionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/promotions")
@RequiredArgsConstructor
public class PromotionController {

    private final PromotionService service;

    // 1. Crear una nueva promoción
    @PostMapping
    public ResponseEntity<PromotionResponseDTO> crearPromocion(@Valid @RequestBody PromotionRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crearPromocion(request));
    }

    // 2. Listar todas las promociones activas e inactivas
    @GetMapping
    public ResponseEntity<List<PromotionResponseDTO>> obtenerTodas() {
        return ResponseEntity.ok(service.obtenerTodas());
    }

    // 3. Buscar una promoción específica por su ID
    @GetMapping("/{id}")
    public ResponseEntity<PromotionResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    // Endpoint para que el order-service busque por el texto del cupón
    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<PromotionResponseDTO> obtenerPorCodigo(@PathVariable String codigo) {
        return ResponseEntity.ok(service.obtenerPorCodigo(codigo));
    }

    // Endpoint para editar un cupón existente
    @PutMapping("/{id}")
    public ResponseEntity<PromotionResponseDTO> actualizarPromocion(
            @PathVariable Long id,
            @Valid @RequestBody PromotionRequestDTO request) {
        return ResponseEntity.ok(service.actualizarPromocion(id, request));
    }

    // Endpoint para apagar un cupón (Patch es ideal para cambiar un solo campo como el estado)
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivarPromocion(@PathVariable Long id) {
        service.desactivarPromocion(id);
        // Devuelve un 204 No Content (Operación exitosa, nada que mostrar)
        return ResponseEntity.noContent().build();
    }
}