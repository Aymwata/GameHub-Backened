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


    // CREACION DE ORDEN DE COMPRA
    @PostMapping
    public ResponseEntity<Orden> crearOrden(@Valid @RequestBody OrdenRequestDTO request) {
        Orden nuevaOrden = ordenService.crearOrden(request);
        return new ResponseEntity<>(nuevaOrden, HttpStatus.CREATED);
    }

    // LISTADO Y FILTRADO
    // Permite obtener el historial o filtrar por ID de usuario y estado actual
    @GetMapping
    public ResponseEntity<List<Orden>> listarOrdenes(
            @RequestParam(required = false) Long usuarioId,
            @RequestParam(required = false) String estado) {
        return ResponseEntity.ok(ordenService.listarOrdenes(usuarioId, estado));
    }

    // BUSQUEDA POR ID
    @GetMapping("/{id}")
    public ResponseEntity<Orden> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ordenService.buscarPorId(id));
    }

    // ACTUALIZACION DE ESTADO
    @PutMapping("/{id}/status")
    public ResponseEntity<Orden> actualizarEstado(
            @PathVariable("id") Long id,
            @RequestParam("estado") String estado) {
        return ResponseEntity.ok(ordenService.actualizarEstado(id, estado));
    }

    // CANCELACION DE ORDEN
    @PutMapping("/{id}/cancel")
    public ResponseEntity<Orden> cancelarOrden(@PathVariable Long id) {
        return ResponseEntity.ok(ordenService.cancelarOrden(id));
    }
}