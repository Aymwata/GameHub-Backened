// Clase principal donde se programa toda la lógica del negocio y las reglas del inventario.
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

    private final MovimientoInventarioRepository movimientoRepository;


    @Transactional
    public InventoryResponseDTO addInitialStock(InventoryRequestDTO dto) {
        log.info("Iniciando proceso de carga de stock para el Producto ID: {}", dto.getProductId());


        this.validarExistenciaEnCatalogo(dto.getProductId());


        Inventory inventory = inventoryRepository.findByProductId(dto.getProductId())
                .orElseGet(Inventory::new);


        this.mapearDatosInventario(inventory, dto);


        Inventory guardado = inventoryRepository.save(inventory);
        log.info("Stock actualizado exitosamente para el producto: {}", dto.getProductId());




        MovimientoInventario movimiento = MovimientoInventario.builder()
                .productId(dto.getProductId())
                .tipo("INGRESO")
                .cantidad(dto.getQuantity())
                .build();
        movimientoRepository.save(movimiento);


        return mapToResponseDTO(guardado);
    }


    @Transactional
    public void reserveStock(Long productId, Integer quantity) {
        log.info("Solicitud de reserva: {} unidades para Producto ID: {}", quantity, productId);

        Inventory inv = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> {
                    log.error("No se encontró registro de inventario para el producto: {}", productId);
                    return new RuntimeException("Error: Registro de inventario inexistente.");
                });


        if (inv.getActualAvailable() < quantity) {
            log.warn("Fallo de reserva: Stock insuficiente para producto {}", productId);
            throw new RuntimeException("Operación fallida: Stock disponible insuficiente para realizar la reserva.");
        }


        inv.setStockReserved(inv.getStockReserved() + quantity);
        inventoryRepository.save(inv);



        MovimientoInventario movimiento = MovimientoInventario.builder()
                .productId(productId)
                .tipo("RESERVA")
                .cantidad(quantity)
                .build();
        movimientoRepository.save(movimiento);

        log.info("Reserva completada satisfactoriamente.");
    }



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


        int stockActual = inventory.getStockAvailable() != null ? inventory.getStockAvailable() : 0;
        inventory.setStockAvailable(stockActual + dto.getQuantity());


        if (inventory.getStockReserved() == null) {
            inventory.setStockReserved(0);
        }

        inventory.setLocation(dto.getLocation());

        if (inventory.getMinStock() == null) {
            inventory.setMinStock(5);
        }
    }


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
                    dto.setFechaMovimiento(mov.getCreatedAt());
                    return dto;
                })
                .collect(Collectors.toList());
    }


    public InventoryResponseDTO obtenerStockPorProducto(Long productId) {
        log.info("Consultando stock actual para el Producto ID: {}", productId);

        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("No se encontró registro de inventario para el producto: " + productId));

        return mapToResponseDTO(inventory);
    }

    public List<InventoryResponseDTO> obtenerTodosLosInventarios() {
        log.info("Consultando el listado completo de inventarios");

        return inventoryRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }
}