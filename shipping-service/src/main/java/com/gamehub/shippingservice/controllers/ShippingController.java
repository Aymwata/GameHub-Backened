package com.gamehub.shippingservice.controllers;

import com.gamehub.shippingservice.models.dto.ShippingRequestDTO;
import com.gamehub.shippingservice.models.dto.ShippingResponseDTO;
import com.gamehub.shippingservice.services.ShippingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
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
@RequestMapping("/api/shippings")
@RequiredArgsConstructor
@Tag(name = "Logística y Despachos", description = "Endpoints para gestionar la creación, estado y seguimiento de los envíos de las compras.")
public class ShippingController {

    private final ShippingService service;

    @Operation(summary = "Generar orden de despacho", description = "Crea un envío validando previamente de forma síncrona que la orden esté PAGADA y el usuario tenga una dirección válida.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Despacho creado exitosamente en estado PREPARANDO"),
            @ApiResponse(responseCode = "409", description = "Violación de regla de negocio (Orden no pagada o sin dirección)", content = @Content),
            @ApiResponse(responseCode = "503", description = "Fallo de comunicación con order-service o user-service", content = @Content)
    })
    @PostMapping
    public ResponseEntity<ShippingResponseDTO> crearDespacho(@Valid @RequestBody ShippingRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crearDespacho(request));
    }

    @Operation(summary = "Buscar despacho por ID", description = "Consulta los detalles de una guía de despacho mediante su identificador interno.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Despacho encontrado"),
            @ApiResponse(responseCode = "404", description = "El despacho no existe", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<ShippingResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @Operation(summary = "Buscar despacho por ID de Orden", description = "Permite a los clientes rastrear el envío vinculado a su número de orden de compra.")
    @ApiResponse(responseCode = "200", description = "Despacho encontrado y retornado")
    @GetMapping("/order/{orderId}")
    public ResponseEntity<ShippingResponseDTO> obtenerPorOrden(@PathVariable Long orderId) {
        return ResponseEntity.ok(service.obtenerPorOrden(orderId));
    }

    @Operation(summary = "Filtrar despachos por estado", description = "Lista masivamente los despachos según su fase logística (PREPARANDO, EN_TRANSITO, ENTREGADO).")
    @ApiResponse(responseCode = "200", description = "Listado filtrado obtenido")
    @GetMapping("/status/{status}")
    public ResponseEntity<List<ShippingResponseDTO>> obtenerPorEstado(@PathVariable String status) {
        return ResponseEntity.ok(service.obtenerPorEstado(status));
    }

    @Operation(summary = "Actualizar estado y Tracking", description = "Permite al operador logístico inyectar el número de seguimiento y transicionar el estado del paquete.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Actualización exitosa. Registra automáticamente la fecha si pasa a ENTREGADO."),
            @ApiResponse(responseCode = "409", description = "El tracking number ya existe en otro despacho", content = @Content)
    })
    @PatchMapping("/{id}")
    public ResponseEntity<ShippingResponseDTO> actualizarDespacho(
            @PathVariable Long id,
            @RequestParam String estado,
            @RequestParam(required = false) String trackingNumber) {
        return ResponseEntity.ok(service.actualizarDespacho(id, estado, trackingNumber));
    }

    @Operation(summary = "Cancelar despacho", description = "Anula la guía de despacho cambiando su estado a CANCELADO en caso de anulación de compra.")
    @ApiResponse(responseCode = "204", description = "Despacho cancelado exitosamente")
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelarDespacho(@PathVariable Long id) {
        service.cancelarDespacho(id);
        return ResponseEntity.noContent().build();
    }
}