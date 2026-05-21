package com.gamehub.shippingservice.controllers;

import com.gamehub.shippingservice.models.dto.ShippingRequestDTO;
import com.gamehub.shippingservice.models.dto.ShippingResponseDTO;
import com.gamehub.shippingservice.services.ShippingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shippings")
@RequiredArgsConstructor
public class ShippingController {

    private final ShippingService service;

    // 1. Crear despacho [cite: 203]
    @PostMapping
    public ResponseEntity<ShippingResponseDTO> crearDespacho(@Valid @RequestBody ShippingRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crearDespacho(request));
    }

    // 2. Buscar despacho por ID [cite: 205]
    @GetMapping("/{id}")
    public ResponseEntity<ShippingResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    // 3. Listar despachos por orden [cite: 204]
    @GetMapping("/order/{orderId}")
    public ResponseEntity<ShippingResponseDTO> obtenerPorOrden(@PathVariable Long orderId) {
        return ResponseEntity.ok(service.obtenerPorOrden(orderId));
    }

    // 4. Listar despachos por estado [cite: 204]
    @GetMapping("/status/{status}")
    public ResponseEntity<List<ShippingResponseDTO>> obtenerPorEstado(@PathVariable String status) {
        return ResponseEntity.ok(service.obtenerPorEstado(status));
    }

    // 5. Actualizar estado y tracking [cite: 206]
    @PatchMapping("/{id}")
    public ResponseEntity<ShippingResponseDTO> actualizarDespacho(
            @PathVariable Long id,
            @RequestParam String estado,
            @RequestParam(required = false) String trackingNumber) {
        return ResponseEntity.ok(service.actualizarDespacho(id, estado, trackingNumber));
    }

    // 6. Cancelar despacho si la orden fue anulada [cite: 207]
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelarDespacho(@PathVariable Long id) {
        service.cancelarDespacho(id);
        // Retorna 204 No Content, estándar HTTP para actualizaciones exitosas sin cuerpo de respuesta
        return ResponseEntity.noContent().build();
    }
}
