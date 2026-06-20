package com.gamehub.orderservice.services;

import com.gamehub.orderservice.client.InventarioClient;
import com.gamehub.orderservice.client.ProductoClient;
import com.gamehub.orderservice.client.PromocionClient;
import com.gamehub.orderservice.client.UsuarioClient;
import com.gamehub.orderservice.exceptions.OrdenException;
import com.gamehub.orderservice.model.DTOs.*;
import com.gamehub.orderservice.model.Orden;
import com.gamehub.orderservice.repository.OrdenRepository;
import com.gamehub.orderservice.service.OrdenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Collections;
import java.util.Optional;
import java.util.List;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrdenServiceTest {

    //MOCKS
    @Mock private OrdenRepository ordenRepository;
    @Mock private UsuarioClient usuarioClient;
    @Mock private ProductoClient productoClient;
    @Mock private InventarioClient inventarioClient;
    @Mock private PromocionClient promocionClient;

    // SUJETO A PROBAR
    @InjectMocks
    private OrdenService ordenService;

    // Variables globales
    private OrdenRequestDTO ordenDto;
    private Orden orden;



    @BeforeEach
    void setUp() {
        // orden de Compra
        DetalleOrdenRequestDTO detalleDto = new DetalleOrdenRequestDTO();
        detalleDto.setProductoId(10L);
        detalleDto.setCantidad(2);

        ordenDto = new OrdenRequestDTO();
        ordenDto.setUsuarioId(1L);
        ordenDto.setDetalles(Collections.singletonList(detalleDto));

        // Preparamos la Orden Simulada en la BD
        orden = new Orden();
        orden.setId(500L);
        orden.setUsuarioId(1L);
        orden.setEstado("PENDIENTE");
        orden.setSubtotal(100.0);
    }

    //  PRUEBAS PARA CREACIÓN DE ORDEN

    @Test
    void crearOrden_exitosamenteSinPromocion() {

        // GIVEN (se simula que los 4 microservicios necesarios responden OK)
        UsuarioClientDTO usuarioSimulado = new UsuarioClientDTO();
        usuarioSimulado.setId(1L);
        usuarioSimulado.setEstado(true);
        when(usuarioClient.obtenerUsuario(1L)).thenReturn(usuarioSimulado);

        ProductoClientDTO productoSimulado = new ProductoClientDTO();
        productoSimulado.setId(10L);
        productoSimulado.setPrecio(50.0);
        productoSimulado.setEstado(true);
        when(productoClient.obtenerProducto(10L)).thenReturn(productoSimulado);

        InventarioClientDTO inventarioSimulado = new InventarioClientDTO();
        inventarioSimulado.setActualAvailable(100);
        when(inventarioClient.consultarStock(10L)).thenReturn(inventarioSimulado);

        // Simulamos la reserva
        doNothing().when(inventarioClient).reservarStock(anyLong(), anyInt());

        // Simulamos el guardado
        when(ordenRepository.save(any(Orden.class))).thenReturn(orden);

        // WHEN
        Orden resultado = ordenService.crearOrden(ordenDto);

        // THEN
        assertNotNull(resultado);
        assertEquals("PENDIENTE", resultado.getEstado());
        verify(ordenRepository, times(1)).save(any(Orden.class));
        verify(inventarioClient, times(1)).reservarStock(10L, 2);
    }

    @Test
    void crearOrden_fallaPorUsuarioInactivo() {

        // GIVEN
        UsuarioClientDTO usuarioSimulado = new UsuarioClientDTO();
        usuarioSimulado.setEstado(false);// <---------------------------------  USUARIO INACTIVO
        when(usuarioClient.obtenerUsuario(1L)).thenReturn(usuarioSimulado);

        // WHEN Y THEN
        OrdenException exception = assertThrows(OrdenException.class, () -> {
            ordenService.crearOrden(ordenDto);
        });

        assertEquals("El usuario está inactivo y no puede comprar", exception.getMessage());
        verify(productoClient, never()).obtenerProducto(anyLong()); // se valida que el flujo se corto
    }

    @Test
    void crearOrden_fallaPorFaltaDeStock() {

        // GIVEN
        UsuarioClientDTO usuarioSimulado = new UsuarioClientDTO();
        usuarioSimulado.setEstado(true);
        when(usuarioClient.obtenerUsuario(1L)).thenReturn(usuarioSimulado);

        ProductoClientDTO productoSimulado = new ProductoClientDTO();
        productoSimulado.setId(10L);
        productoSimulado.setPrecio(50.0);
        productoSimulado.setEstado(true);
        when(productoClient.obtenerProducto(10L)).thenReturn(productoSimulado);

        InventarioClientDTO inventarioSimulado = new InventarioClientDTO();
        inventarioSimulado.setActualAvailable(1); // <--------------------------------------    Solo hay 1 pero queremos 2
        when(inventarioClient.consultarStock(10L)).thenReturn(inventarioSimulado);

        // WHEN Y THEN
        OrdenException exception = assertThrows(OrdenException.class, () -> {
            ordenService.crearOrden(ordenDto);
        });

        assertEquals("Stock insuficiente para el producto ID 10", exception.getMessage());
    }


    //  PRUEBAS PARA BÚSQUEDA Y FILTRADO

    @Test
    void buscarPorId_exitoso() {

        // GIVEN
        when(ordenRepository.findById(500L)).thenReturn(Optional.of(orden));

        // WHEN
        Orden resultado = ordenService.buscarPorId(500L);

        // THEN
        assertNotNull(resultado);
        assertEquals(500L, resultado.getId());
    }

    @Test
    void listarOrdenes_filtraPorEstado() {

        // GIVEN
        List<Orden> listaSimulada = Arrays.asList(orden);
        when(ordenRepository.findByEstado("PENDIENTE")).thenReturn(listaSimulada);

        // WHEN
        List<Orden> resultado = ordenService.listarOrdenes(null, "PENDIENTE");

        // THEN
        assertEquals(1, resultado.size());
    }

    //  PRUEBAS PARA ACTUALIZACIÓN Y CANCELACIÓN

    @Test
    void actualizarEstado_exitoso() {

        // GIVEN
        when(ordenRepository.findById(500L)).thenReturn(Optional.of(orden));
        when(ordenRepository.save(any(Orden.class))).thenReturn(orden);

        // WHEN
        Orden resultado = ordenService.actualizarEstado(500L, "ENVIADA");

        // THEN
        assertEquals("ENVIADA", resultado.getEstado());
    }

    @Test
    void actualizarEstado_fallaPorqueEstaPagada() {

        // GIVEN
        orden.setEstado("PAGADA");
        when(ordenRepository.findById(500L)).thenReturn(Optional.of(orden));

        // WHEN Y THEN
        OrdenException exception = assertThrows(OrdenException.class, () -> {
            ordenService.actualizarEstado(500L, "ENVIADA");
        });

        assertEquals("No se puede modificar una orden que ya está Pagada o Cancelada", exception.getMessage());
    }

    @Test
    void cancelarOrden_exitoso() {

        // GIVEN
        when(ordenRepository.findById(500L)).thenReturn(Optional.of(orden));
        when(ordenRepository.save(any(Orden.class))).thenReturn(orden);

        // WHEN
        Orden resultado = ordenService.cancelarOrden(500L);

        // THEN
        assertEquals("CANCELADA", resultado.getEstado());
    }
}