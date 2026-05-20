package com.gamehub.inventoryservice.services;

import com.gamehub.inventoryservice.client.ProductClient;
import com.gamehub.inventoryservice.models.Inventory;
import com.gamehub.inventoryservice.models.MovimientoInventario;
import com.gamehub.inventoryservice.models.dto.InventoryRequestDTO;
import com.gamehub.inventoryservice.models.dto.InventoryResponseDTO;
import com.gamehub.inventoryservice.models.dto.MovimientoResponseDTO;
import com.gamehub.inventoryservice.repositories.InventoryRepository;
import com.gamehub.inventoryservice.repositories.MovimientoInventarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductClient productClient;
    // AGREGA ESTA LÍNEA:
    private final MovimientoInventarioRepository movimientoRepository;

    /**
     * Registra o actualiza el stock validando la existencia del producto mediante Feign.
     */
    @Transactional
    public InventoryResponseDTO addInitialStock(InventoryRequestDTO dto) {
        log.info("Iniciando proceso de carga de stock para el Producto ID: {}", dto.getProductId());

        // 1. Validación por red (Feign) para asegurarnos de que el producto existe
        this.validarExistenciaEnCatalogo(dto.getProductId());

        // 2. Buscar si ya existe inventario para este producto, si no, crear uno vacío
        Inventory inventory = inventoryRepository.findByProductId(dto.getProductId())
                .orElseGet(Inventory::new);

        // 3. Aplicar la lógica de negocio (aquí arreglamos el bug matemático)
        this.mapearDatosInventario(inventory, dto);

        // 4. Guardar en la base de datos
        Inventory guardado = inventoryRepository.save(inventory);
        log.info("Stock actualizado exitosamente para el producto: {}", dto.getProductId());

        // ... (código existente donde se guarda el inventory) ...

        // REGISTRO DEL HISTORIAL
        MovimientoInventario movimiento = MovimientoInventario.builder()
                .productId(dto.getProductId())
                .tipo("INGRESO")
                .cantidad(dto.getQuantity())
                .build();
        movimientoRepository.save(movimiento);

        // 5. Devolver el DTO para evitar la "Fuga de la Entidad" (Regla de la rúbrica)
        return mapToResponseDTO(guardado);
    }

    /**
     * Gestiona la reserva de unidades cuando se está creando una orden.
     */
    @Transactional
    public void reserveStock(Long productId, Integer quantity) {
        log.info("Solicitud de reserva: {} unidades para Producto ID: {}", quantity, productId);

        Inventory inv = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> {
                    log.error("No se encontró registro de inventario para el producto: {}", productId);
                    return new RuntimeException("Error: Registro de inventario inexistente.");
                });

        // Validar que realmente haya suficientes productos libres para reservar
        if (inv.getActualAvailable() < quantity) {
            log.warn("Fallo de reserva: Stock insuficiente para producto {}", productId);
            throw new RuntimeException("Operación fallida: Stock disponible insuficiente para realizar la reserva.");
        }

        // Sumar a lo reservado
        inv.setStockReserved(inv.getStockReserved() + quantity);
        inventoryRepository.save(inv);
        // ... (código existente donde se guarda el inv) ...

        // REGISTRO DEL HISTORIAL
        MovimientoInventario movimiento = MovimientoInventario.builder()
                .productId(productId)
                .tipo("RESERVA")
                .cantidad(quantity)
                .build();
        movimientoRepository.save(movimiento);

        log.info("Reserva completada satisfactoriamente.");
    }

    // --- MÉTODOS PRIVADOS DE SOPORTE ---

    private void validarExistenciaEnCatalogo(Long productId) {
        try {
            productClient.getProductoById(productId);
        } catch (Exception e) {
            log.error("Error al comunicarse con Product-Service o producto inexistente: ", e);
            throw new RuntimeException("Fallo de validación: El producto ID " + productId + " no existe o el servicio no responde.");
        }
    }

    private void mapearDatosInventario(Inventory inventory, InventoryRequestDTO dto) {
        inventory.setProductId(dto.getProductId());

        // CORRECCIÓN MATEMÁTICA: Sumamos el stock, no lo sobrescribimos.
        int stockActual = inventory.getStockAvailable() != null ? inventory.getStockAvailable() : 0;
        inventory.setStockAvailable(stockActual + dto.getQuantity());

        // Si es la primera vez que se crea, inicializamos lo reservado en 0 para evitar errores Null
        if (inventory.getStockReserved() == null) {
            inventory.setStockReserved(0);
        }

        inventory.setLocation(dto.getLocation());

        if (inventory.getMinStock() == null) {
            inventory.setMinStock(5); // Umbral de stock crítico por defecto
        }
    }

    // NUEVO MÉTODO: Traduce la base de datos (Entidad) a un formato seguro (DTO)
    private InventoryResponseDTO mapToResponseDTO(Inventory inventory) {
        InventoryResponseDTO response = new InventoryResponseDTO();
        response.setId(inventory.getId());
        response.setProductId(inventory.getProductId());
        response.setStockAvailable(inventory.getStockAvailable());
        response.setStockReserved(inventory.getStockReserved());
        response.setActualAvailable(inventory.getActualAvailable());
        response.setLocation(inventory.getLocation());
        return response;
    }

    public List<MovimientoResponseDTO> obtenerHistorialMovimientos(Long productId) {
        return movimientoRepository.findByProductId(productId)
                .stream()
                .map(mov -> {
                    MovimientoResponseDTO dto = new MovimientoResponseDTO();
                    dto.setId(mov.getId());
                    dto.setProductId(mov.getProductId());
                    dto.setTipo(mov.getTipo());
                    dto.setCantidad(mov.getCantidad());
                    dto.setFechaMovimiento(mov.getCreatedAt()); // Extraemos la fecha automática de la clase Audit
                    return dto;
                })
                .collect(Collectors.toList());
    }
}