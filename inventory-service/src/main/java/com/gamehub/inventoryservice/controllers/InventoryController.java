package com.gamehub.inventoryservice.controllers;

import com.gamehub.inventoryservice.models.dto.InventoryRequestDTO;
import com.gamehub.inventoryservice.models.dto.InventoryResponseDTO;
import com.gamehub.inventoryservice.models.dto.MovimientoResponseDTO;
import com.gamehub.inventoryservice.services.InventoryService;
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
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventario", description = "Operaciones relacionadas con el stock y movimientos de productos")
public class InventoryController {

    private final InventoryService inventoryService;

    @Operation(summary = "Configurar inventario inicial", description = "Añade stock inicial a un producto y genera el movimiento de ingreso")
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Stock creado/actualizado")})
    @PostMapping("/setup")
    public ResponseEntity<EntityModel<InventoryResponseDTO>> setupInventory(@Valid @RequestBody InventoryRequestDTO dto) {
        InventoryResponseDTO response = inventoryService.addInitialStock(dto);

        EntityModel<InventoryResponseDTO> resource = EntityModel.of(response,
                linkTo(methodOn(InventoryController.class).consultarStock(response.getProductId())).withSelfRel(),
                linkTo(methodOn(InventoryController.class).listarTodoElInventario()).withRel("todo-el-inventario"));

        return new ResponseEntity<>(resource, HttpStatus.CREATED);
    }

    @Operation(summary = "Reservar stock", description = "Reserva una cantidad específica de un producto")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Reserva exitosa")})
    @PutMapping("/reserve")
    public ResponseEntity<Map<String, String>> reserve(
            @RequestParam Long productId,
            @RequestParam Integer quantity) {

        inventoryService.reserveStock(productId, quantity);
        return ResponseEntity.ok(Map.of(
                "message", "Stock reservado correctamente",
                "productId", productId.toString(),
                "status", "SUCCESS"
        ));
    }

    @Operation(summary = "Ver historial de movimientos", description = "Obtiene todos los ingresos y reservas de un producto")
    @GetMapping("/{productId}/movimientos")
    public ResponseEntity<CollectionModel<EntityModel<MovimientoResponseDTO>>> verHistorial(@PathVariable Long productId) {
        List<EntityModel<MovimientoResponseDTO>> movimientos = inventoryService.obtenerHistorialMovimientos(productId).stream()
                .map(mov -> EntityModel.of(mov))
                .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(movimientos,
                linkTo(methodOn(InventoryController.class).verHistorial(productId)).withSelfRel()));
    }

    @Operation(summary = "Consultar stock por producto", description = "Obtiene el inventario actual de un producto específico")
    @GetMapping("/product/{productId}")
    public ResponseEntity<EntityModel<InventoryResponseDTO>> consultarStock(@PathVariable Long productId) {
        InventoryResponseDTO response = inventoryService.obtenerStockPorProducto(productId);

        EntityModel<InventoryResponseDTO> resource = EntityModel.of(response,
                linkTo(methodOn(InventoryController.class).consultarStock(productId)).withSelfRel(),
                linkTo(methodOn(InventoryController.class).verHistorial(productId)).withRel("historial-movimientos"),
                linkTo(methodOn(InventoryController.class).listarTodoElInventario()).withRel("todo-el-inventario"));

        return ResponseEntity.ok(resource);
    }

    @Operation(summary = "Listar todo el inventario", description = "Obtiene el stock de todos los productos en el sistema")
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<InventoryResponseDTO>>> listarTodoElInventario() {
        List<EntityModel<InventoryResponseDTO>> inventarios = inventoryService.obtenerTodosLosInventarios().stream()
                .map(inv -> EntityModel.of(inv,
                        linkTo(methodOn(InventoryController.class).consultarStock(inv.getProductId())).withSelfRel()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(inventarios,
                linkTo(methodOn(InventoryController.class).listarTodoElInventario()).withSelfRel()));
    }
}