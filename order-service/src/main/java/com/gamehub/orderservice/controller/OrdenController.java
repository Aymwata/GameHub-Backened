package com.gamehub.orderservice.controller;

import com.gamehub.orderservice.model.DTOs.OrdenRequestDTO;
import com.gamehub.orderservice.model.Orden;
import com.gamehub.orderservice.service.OrdenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor

@Tag(name = "Gestion de Ordenes", description = "Endpoints para el procesamiento, seguimiento y administración de órdenes de compra en GameHub.")
public class OrdenController {

    private final OrdenService ordenService;



    // CREACION DE ORDEN DE COMPRA
    @Operation(summary = "Crear una nueva orden", description = "Genera una nueva orden de compra procesando los productos solicitados y calculando los totales.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Orden de compra creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de la orden inválidos (ej. falta de stock o formato incorrecto)"),
            @ApiResponse(responseCode = "404", description = "Usuario o productos no encontrados")
    })
    @PostMapping
    public ResponseEntity<Orden> crearOrden(@Valid @RequestBody OrdenRequestDTO request) {
        Orden nuevaOrden = ordenService.crearOrden(request);
        return new ResponseEntity<>(nuevaOrden, HttpStatus.CREATED);
    }



    // LISTADO Y FILTRADO
    @Operation(summary = "Listar órdenes de compra", description = "Obtiene el historial de órdenes. Permite filtrar los resultados especificando un ID de usuario o el estado actual de la orden.")
    @ApiResponse(responseCode = "200", description = "Lista de órdenes recuperada exitosamente")
    @GetMapping
    public ResponseEntity<List<Orden>> listarOrdenes(
            @Parameter(description = "ID del usuario para ver su historial de compras", example = "1") @RequestParam(required = false) Long usuarioId,
            @Parameter(description = "Filtro por estado de la orden (ej. PENDIENTE, EN_PROCESO, COMPLETADO, CANCELADO)", example = "COMPLETADO") @RequestParam(required = false) String estado) {
        return ResponseEntity.ok(ordenService.listarOrdenes(usuarioId, estado));
    }



    // BUSQUEDA POR ID
    @Operation(summary = "Buscar orden por ID", description = "Recupera los detalles completos de una orden específica utilizando su número identificador único.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orden encontrada"),
            @ApiResponse(responseCode = "404", description = "La orden solicitada no existe")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Orden> buscarPorId(
            @Parameter(description = "ID único de la orden de compra", example = "1050") @PathVariable Long id) {
        return ResponseEntity.ok(ordenService.buscarPorId(id));
    }



    // ACTUALIZACION DE ESTADO
    @Operation(summary = "Actualizar estado de la orden", description = "Avanza o retrocede el estado de una orden (ej. de PENDIENTE a EN_PROCESO).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado de la orden actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Transición de estado no válida o estado inexistente"),
            @ApiResponse(responseCode = "404", description = "La orden a actualizar no existe")
    })
    @PutMapping("/{id}/status")
    public ResponseEntity<Orden> actualizarEstado(
            @Parameter(description = "ID de la orden", example = "1050") @PathVariable("id") Long id,
            @Parameter(description = "Nuevo estado a asignar", example = "ENVIADO") @RequestParam("estado") String estado) {
        return ResponseEntity.ok(ordenService.actualizarEstado(id, estado));
    }



    // CANCELACION DE ORDEN
    @Operation(summary = "Cancelar orden", description = "Anula una orden de compra activa. Generalmente cambia su estado a CANCELADO y puede disparar eventos de devolución de stock.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orden cancelada exitosamente"),
            @ApiResponse(responseCode = "400", description = "La orden no se puede cancelar (ej. ya fue enviada o ya estaba cancelada)"),
            @ApiResponse(responseCode = "404", description = "La orden no existe")
    })
    @PutMapping("/{id}/cancel")
    public ResponseEntity<Orden> cancelarOrden(
            @Parameter(description = "ID de la orden a cancelar", example = "1050") @PathVariable Long id) {
        return ResponseEntity.ok(ordenService.cancelarOrden(id));
    }
}