package com.gamehub.paymentservice;


import com.gamehub.paymentservice.client.OrderClient;
import com.gamehub.paymentservice.models.Pago;
import com.gamehub.paymentservice.models.dto.*;
import com.gamehub.paymentservice.repositories.PagoRepository;
import com.gamehub.paymentservice.services.PagoService;
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
public class PagoServiceTest {

    @Mock
    private PagoRepository pagoRepository;

    @Mock
    private OrderClient orderClient;

    @InjectMocks
    private PagoService pagoService;

    @Test
    @DisplayName("Test 1: Buscar un pago por ID exitosamente")
    void buscarPorIdExitoso() {
        // 1. Arrange (Preparar)
        Pago pagoSimulado = new Pago();
        pagoSimulado.setId(1L);
        pagoSimulado.setOrdenId(10L);
        pagoSimulado.setMonto(5000.0);
        pagoSimulado.setEstado("APPROVED");

        when(pagoRepository.findById(1L)).thenReturn(Optional.of(pagoSimulado));

        PagoResponseDTO resultado = pagoService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("APPROVED", resultado.getEstado());

        verify(pagoRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Test 2: Procesar pago exitosamente")
    void procesarPagoExitoso() {
        PagoRequestDTO requestDTO = new PagoRequestDTO();
        requestDTO.setOrdenId(10L);
        requestDTO.setMonto(5000.0);
        requestDTO.setMetodo("CREDIT_CARD");
        OrdenClientDTO ordenSimulada = new OrdenClientDTO();
        ordenSimulada.setId(10L);
        ordenSimulada.setTotal(5000.0); // El monto coincide
        ordenSimulada.setUsuarioId(99L);

        Pago pagoGuardado = new Pago();
        pagoGuardado.setId(1L);
        pagoGuardado.setOrdenId(10L);
        pagoGuardado.setMonto(5000.0);
        pagoGuardado.setEstado("APPROVED");
        pagoGuardado.setCodigoTransaccion("TX-12345678");

        when(orderClient.getOrdenById(10L)).thenReturn(ordenSimulada);
        when(pagoRepository.existsByOrdenIdAndEstado(10L, "APPROVED")).thenReturn(false);
        when(pagoRepository.save(any(Pago.class))).thenReturn(pagoGuardado);

        PagoResponseDTO resultado = pagoService.procesarPago(requestDTO);

        assertNotNull(resultado);
        assertEquals("APPROVED", resultado.getEstado());
        assertEquals("TX-12345678", resultado.getCodigoTransaccion());


        verify(orderClient, times(1)).actualizarEstadoOrden(10L, "PAID");
    }

    @Test
    @DisplayName("Test 3: Anular un pago exitosamente")
    void anularPagoExitoso() {

        Pago pagoSimulado = new Pago();
        pagoSimulado.setId(1L);
        pagoSimulado.setOrdenId(10L);
        pagoSimulado.setEstado("APPROVED"); // Estado actual

        when(pagoRepository.findById(1L)).thenReturn(Optional.of(pagoSimulado));
        when(pagoRepository.save(any(Pago.class))).thenReturn(pagoSimulado);


        PagoResponseDTO resultado = pagoService.anularPago(1L);


        assertEquals("ANULADO", resultado.getEstado());
        verify(orderClient, times(1)).actualizarEstadoOrden(10L, "CANCELED");
    }
}