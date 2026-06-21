package com.gamehub.paymentservice.controllers;

import com.gamehub.paymentservice.models.dto.*;
import com.gamehub.paymentservice.services.PagoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

// Importaciones estáticas necesarias para HATEOAS
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Pagos", description = "API para la gestión y procesamiento de transacciones de pago")
public class PaymentController {

    private final PagoService pagoService;

    @Operation(summary = "Crear un nuevo pago", description = "Procesa una nueva transacción y la registra en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pago procesado y creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de petición inválidos")
    })
    @PostMapping
    public ResponseEntity<EntityModel<PagoResponseDTO>> crearPago(@Valid @RequestBody PagoRequestDTO dto) {
        PagoResponseDTO response = pagoService.procesarPago(dto);

        // HATEOAS: Agregamos enlaces al pago recién creado
        EntityModel<PagoResponseDTO> resource = EntityModel.of(response,
                linkTo(methodOn(PaymentController.class).obtenerPorId(response.getId())).withSelfRel(),
                linkTo(methodOn(PaymentController.class).obtenerPagos(null, null, null)).withRel("todos-los-pagos")
        );

        return new ResponseEntity<>(resource, HttpStatus.CREATED);
    }

    @Operation(summary = "Listar pagos", description = "Obtiene todos los pagos. Permite filtrar por ordenId, clienteId o estado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de pagos obtenida exitosamente")
    })
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<PagoResponseDTO>>> obtenerPagos(
            @RequestParam(required = false) Long ordenId,
            @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) String estado) {

        List<PagoResponseDTO> pagos = pagoService.listarPagos(ordenId, clienteId, estado);

        // HATEOAS: Transformamos la lista a EntityModel para cada pago
        List<EntityModel<PagoResponseDTO>> pagosModel = pagos.stream()
                .map(pago -> EntityModel.of(pago,
                        // ¡OJO ACÁ! Si tu DTO no tiene getId(), cámbialo por el método correcto
                        linkTo(methodOn(PaymentController.class).obtenerPorId(pago.getId())).withSelfRel()))
                .collect(Collectors.toList());

        // HATEOAS: Agregamos el enlace general a la colección
        CollectionModel<EntityModel<PagoResponseDTO>> collectionModel = CollectionModel.of(pagosModel,
                linkTo(methodOn(PaymentController.class).obtenerPagos(ordenId, clienteId, estado)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    @Operation(summary = "Obtener pago por ID", description = "Busca y retorna un pago específico según su identificador único")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pago encontrado"),
            @ApiResponse(responseCode = "404", description = "Pago no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<PagoResponseDTO>> obtenerPorId(@PathVariable Long id) {
        PagoResponseDTO pago = pagoService.buscarPorId(id);

        // HATEOAS: Enlaces para un recurso individual
        EntityModel<PagoResponseDTO> resource = EntityModel.of(pago,
                linkTo(methodOn(PaymentController.class).obtenerPorId(id)).withSelfRel(),
                linkTo(methodOn(PaymentController.class).obtenerPagos(null, null, null)).withRel("todos-los-pagos")
        );

        return ResponseEntity.ok(resource);
    }

    @Operation(summary = "Anular un pago", description = "Cambia el estado de un pago existente a 'ANULADO'")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pago anulado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Pago no encontrado para anular")
    })
    @PutMapping("/{id}/anular")
    public ResponseEntity<EntityModel<PagoResponseDTO>> anularPago(@PathVariable Long id) {
        PagoResponseDTO pago = pagoService.anularPago(id);

        // HATEOAS: Enlaces tras actualizar el estado
        EntityModel<PagoResponseDTO> resource = EntityModel.of(pago,
                linkTo(methodOn(PaymentController.class).obtenerPorId(id)).withSelfRel(),
                linkTo(methodOn(PaymentController.class).obtenerPagos(null, null, null)).withRel("todos-los-pagos")
        );

        return ResponseEntity.ok(resource);
    }
}