package com.gamehub.orderservice.controller;

import com.gamehub.orderservice.model.DTOs.OrdenRequestDTO;
import com.gamehub.orderservice.model.Orden;
import com.gamehub.orderservice.service.OrdenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrdenController {

    private final OrdenService ordenService;

    // 1. Crear orden
    @PostMapping
    public ResponseEntity<Orden> crearOrden(@Valid @RequestBody OrdenRequestDTO request) {
        Orden nuevaOrden = ordenService.crearOrden(request);
        return new ResponseEntity<>(nuevaOrden, HttpStatus.CREATED);
    }

    // 2. Listar órdenes por cliente o estado
    @GetMapping
    public ResponseEntity<List<Orden>> listarOrdenes(
            @RequestParam(required = false) Long usuarioId,
            @RequestParam(required = false) String estado) {
        return ResponseEntity.ok(ordenService.listarOrdenes(usuarioId, estado));
    }

    // 3. Buscar orden por ID
    @GetMapping("/{id}")
    public ResponseEntity<Orden> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ordenService.buscarPorId(id));
    }

    // 4. Actualizar estado
// 4. Actualizar estado (Versión Blindada)
    @PutMapping("/{id}/status")
    public ResponseEntity<Orden> actualizarEstado(
            @PathVariable("id") Long id,
            @RequestParam("estado") String estado) {
        return ResponseEntity.ok(ordenService.actualizarEstado(id, estado));
    }

    // 5. Cancelar orden
    @PutMapping("/{id}/cancel")
    public ResponseEntity<Orden> cancelarOrden(@PathVariable Long id) {
        return ResponseEntity.ok(ordenService.cancelarOrden(id));
    }
}