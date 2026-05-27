// Expone las rutas para que los clientes apliquen cupones de descuento en sus compras.
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


    @PostMapping
    public ResponseEntity<PromotionResponseDTO> crearPromocion(@Valid @RequestBody PromotionRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crearPromocion(request));
    }


    @GetMapping
    public ResponseEntity<List<PromotionResponseDTO>> obtenerTodas() {
        return ResponseEntity.ok(service.obtenerTodas());
    }


    @GetMapping("/{id}")
    public ResponseEntity<PromotionResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }


    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<PromotionResponseDTO> obtenerPorCodigo(@PathVariable String codigo) {
        return ResponseEntity.ok(service.obtenerPorCodigo(codigo));
    }


    @PutMapping("/{id}")
    public ResponseEntity<PromotionResponseDTO> actualizarPromocion(
            @PathVariable Long id,
            @Valid @RequestBody PromotionRequestDTO request) {
        return ResponseEntity.ok(service.actualizarPromocion(id, request));
    }


    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivarPromocion(@PathVariable Long id) {
        service.desactivarPromocion(id);

        return ResponseEntity.noContent().build();
    }
}