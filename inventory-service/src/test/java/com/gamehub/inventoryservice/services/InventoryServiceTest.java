package com.gamehub.inventoryservice.services;

import com.gamehub.inventoryservice.client.ProductClient;
import com.gamehub.inventoryservice.models.Inventory;
import com.gamehub.inventoryservice.models.MovimientoInventario;
import com.gamehub.inventoryservice.models.dto.InventoryResponseDTO;
import com.gamehub.inventoryservice.repositories.InventoryRepository;
import com.gamehub.inventoryservice.repositories.MovimientoInventarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private MovimientoInventarioRepository movimientoRepository;

    @Mock
    private ProductClient productClient;

    @InjectMocks
    private InventoryService inventoryService;

    @Test
    @DisplayName("Test: Obtener stock por producto exitosamente")
    void obtenerStockPorProductoExitoso() {

        Inventory inventarioSimulado = new Inventory();
        inventarioSimulado.setId(1L);
        inventarioSimulado.setProductId(10L);
        inventarioSimulado.setStockAvailable(100);
        inventarioSimulado.setStockReserved(10);
        inventarioSimulado.setLocation("Bodega Central");

        when(inventoryRepository.findByProductId(10L)).thenReturn(Optional.of(inventarioSimulado));

        InventoryResponseDTO resultado = inventoryService.obtenerStockPorProducto(10L);


        assertNotNull(resultado);
        assertEquals(10L, resultado.getProductId());
        assertEquals(100, resultado.getStockAvailable());
        assertEquals(10, resultado.getStockReserved());

        assertEquals(90, resultado.getActualAvailable());

        verify(inventoryRepository, times(1)).findByProductId(10L);
    }

    @Test
    @DisplayName("Test: Reservar stock exitosamente")
    void reservarStockExitoso() {

        Inventory inventarioSimulado = new Inventory();
        inventarioSimulado.setId(1L);
        inventarioSimulado.setProductId(10L);
        inventarioSimulado.setStockAvailable(50);
        inventarioSimulado.setStockReserved(5);

        when(inventoryRepository.findByProductId(10L)).thenReturn(Optional.of(inventarioSimulado));
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventarioSimulado);
        when(movimientoRepository.save(any(MovimientoInventario.class))).thenReturn(new MovimientoInventario());


        inventoryService.reserveStock(10L, 15);


        assertEquals(20, inventarioSimulado.getStockReserved());
        verify(inventoryRepository, times(1)).save(inventarioSimulado);
        verify(movimientoRepository, times(1)).save(any(MovimientoInventario.class)); // Verificamos que se guardó el historial
    }

    @Test
    @DisplayName("Test: Fallo al reservar por stock insuficiente")
    void reservarStockFalloPorStockInsuficiente() {

        Inventory inventarioSimulado = new Inventory();
        inventarioSimulado.setId(1L);
        inventarioSimulado.setProductId(10L);
        inventarioSimulado.setStockAvailable(20);
        inventarioSimulado.setStockReserved(15);


        when(inventoryRepository.findByProductId(10L)).thenReturn(Optional.of(inventarioSimulado));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            inventoryService.reserveStock(10L, 10);
        });

        assertTrue(exception.getMessage().contains("Stock disponible insuficiente"));
        verify(inventoryRepository, never()).save(any());
    }
}