// Expone los endpoints REST (URLs) para que Postman o el Frontend consulten el stock.
package com.gamehub.inventoryservice.controllers;

import com.gamehub.inventoryservice.models.Inventory;
import com.gamehub.inventoryservice.models.dto.InventoryRequestDTO;
import com.gamehub.inventoryservice.models.dto.InventoryResponseDTO;
import com.gamehub.inventoryservice.models.dto.MovimientoResponseDTO;
import com.gamehub.inventoryservice.services.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;


    @PostMapping("/setup")
    public ResponseEntity<InventoryResponseDTO> setupInventory(@Valid @RequestBody InventoryRequestDTO dto) {
        InventoryResponseDTO response = inventoryService.addInitialStock(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


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

    @GetMapping("/{productId}/movimientos")
    public ResponseEntity<List<MovimientoResponseDTO>> verHistorial(@PathVariable Long productId) {
        return ResponseEntity.ok(inventoryService.obtenerHistorialMovimientos(productId));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<InventoryResponseDTO> consultarStock(@PathVariable Long productId) {
        return ResponseEntity.ok(inventoryService.obtenerStockPorProducto(productId));
    }

    @GetMapping
    public ResponseEntity<List<InventoryResponseDTO>> listarTodoElInventario() {
        return ResponseEntity.ok(inventoryService.obtenerTodosLosInventarios());
    }

}