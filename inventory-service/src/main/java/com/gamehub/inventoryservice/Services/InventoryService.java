package com.gamehub.inventoryservice.Services;

import com.gamehub.inventoryservice.Models.Inventory;
import com.gamehub.inventoryservice.Models.InventoryRequestDTO;
import com.gamehub.inventoryservice.Repositories.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio de gestión de inventario.
 * Centraliza la lógica de control de existencias y validación con servicios externos.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductClient productClient;

    /**
     * Registra el stock inicial validando la existencia del producto mediante Feign.
     */
    @Transactional
    public Inventory addInitialStock(InventoryRequestDTO dto) {
        log.info("Iniciando proceso de carga de stock para el Producto ID: {}", dto.getProductId());

        // Validación de consistencia sincrónica con Microservicio de Productos
        this.validarExistenciaEnCatalogo(dto.getProductId());

        // Recuperar registro existente o instanciar uno nuevo (Lazy Initialization)
        Inventory inventory = inventoryRepository.findByProductId(dto.getProductId())
                .orElseGet(Inventory::new);

        this.mapearDatosInventario(inventory, dto);

        log.info("Stock actualizado exitosamente para el producto: {}", dto.getProductId());
        return inventoryRepository.save(inventory);
    }

    /**
     * Gestiona la reserva de unidades, asegurando la integridad del stock disponible.
     */
    @Transactional
    public void reserveStock(Long productId, Integer quantity) {
        log.info("Solicitud de reserva: {} unidades para Producto ID: {}", quantity, productId);

        Inventory inv = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> {
                    log.error("No se encontró registro de inventario para el producto: {}", productId);
                    return new RuntimeException("Error: Registro de inventario inexistente.");
                });

        // Lógica de negocio: Verificar disponibilidad real
        if (inv.getActualAvailable() < quantity) {
            log.warn("Fallo de reserva: Stock insuficiente para producto {}", productId);
            throw new RuntimeException("Operación fallida: Stock disponible insuficiente para realizar la reserva.");
        }

        inv.setStockReserved(inv.getStockReserved() + quantity);
        inventoryRepository.save(inv);
        log.info("Reserva completada satisfactoriamente.");
    }

    // --- Métodos Privados de Soporte (Clean Code) ---

    private void validarExistenciaEnCatalogo(Long productId) {
        try {
            productClient.getProductoById(productId);
        } catch (Exception e) {
            log.error("El producto con ID {} no fue localizado en el servicio de catálogo.", productId);
            throw new RuntimeException("Violación de integridad: El producto no existe en el catálogo maestro.");
        }
    }

    private void mapearDatosInventario(Inventory inventory, InventoryRequestDTO dto) {
        inventory.setProductId(dto.getProductId());
        inventory.setStockAvailable(dto.getQuantity());
        inventory.setStockReserved(0);
        inventory.setLocation(dto.getLocation());
        inventory.setMinStock(5); // Umbral de stock crítico por defecto
    }
}