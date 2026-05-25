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


    @PostMapping
    public ResponseEntity<ShippingResponseDTO> crearDespacho(@Valid @RequestBody ShippingRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crearDespacho(request));
    }


    @GetMapping("/{id}")
    public ResponseEntity<ShippingResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }


    @GetMapping("/order/{orderId}")
    public ResponseEntity<ShippingResponseDTO> obtenerPorOrden(@PathVariable Long orderId) {
        return ResponseEntity.ok(service.obtenerPorOrden(orderId));
    }


    @GetMapping("/status/{status}")
    public ResponseEntity<List<ShippingResponseDTO>> obtenerPorEstado(@PathVariable String status) {
        return ResponseEntity.ok(service.obtenerPorEstado(status));
    }


    @PatchMapping("/{id}")
    public ResponseEntity<ShippingResponseDTO> actualizarDespacho(
            @PathVariable Long id,
            @RequestParam String estado,
            @RequestParam(required = false) String trackingNumber) {
        return ResponseEntity.ok(service.actualizarDespacho(id, estado, trackingNumber));
    }


    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelarDespacho(@PathVariable Long id) {
        service.cancelarDespacho(id);
        return ResponseEntity.noContent().build();
    }
}
