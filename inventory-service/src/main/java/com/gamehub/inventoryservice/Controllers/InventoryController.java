package com.gamehub.inventoryservice.Controllers;

import com.gamehub.inventoryservice.Models.Inventory;
import com.gamehub.inventoryservice.Models.InventoryRequestDTO;
import com.gamehub.inventoryservice.Services.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor // Sustituye @Autowired por inyección por constructor
public class InventoryController {

    private final InventoryService inventoryService;


    @PostMapping("/setup")
    public ResponseEntity<Inventory> setupInventory(@Valid @RequestBody InventoryRequestDTO dto) {
        Inventory response = inventoryService.addInitialStock(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @PatchMapping("/reserve")
    public ResponseEntity<Map<String, String>> reserve(
            @RequestParam Long productId,
            @RequestParam Integer quantity) {

        inventoryService.reserveStock(productId, quantity);

        // Devolvemos un JSON en lugar de un String plano (Mejor práctica)
        return ResponseEntity.ok(Map.of(
                "message", "Stock reservado correctamente",
                "productId", productId.toString(),
                "status", "SUCCESS"
        ));
    }
}