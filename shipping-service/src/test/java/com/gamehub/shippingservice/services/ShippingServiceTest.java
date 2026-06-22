package com.gamehub.shippingservice.services;

import com.gamehub.shippingservice.client.OrderClient;
import com.gamehub.shippingservice.client.UserClient;
import com.gamehub.shippingservice.models.Shipping;
import com.gamehub.shippingservice.models.dto.AddressClientDTO;
import com.gamehub.shippingservice.models.dto.OrderClientDTO;
import com.gamehub.shippingservice.models.dto.ShippingRequestDTO;
import com.gamehub.shippingservice.models.dto.ShippingResponseDTO;
import com.gamehub.shippingservice.repositories.ShippingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShippingServiceTest {

    @Mock
    private ShippingRepository repository; // Simulamos BD

    @Mock
    private OrderClient orderClient; // Simulamos red con Order-Service

    @Mock
    private UserClient userClient; // Simulamos red con User-Service

    @InjectMocks
    private ShippingService service;

    @Test
    void crearDespacho_Exito() {
        // --- GIVEN ---
        ShippingRequestDTO request = new ShippingRequestDTO();
        request.setOrderId(100L);
        request.setUserId(5L);
        request.setCarrier("Blue Express");

        // 1. Simulamos que la orden SÍ está pagada
        OrderClientDTO ordenMock = new OrderClientDTO();
        ordenMock.setId(100L);
        ordenMock.setEstado("PAID");

        // 2. Simulamos la lista de direcciones del usuario
        AddressClientDTO direccionMock = new AddressClientDTO();
        direccionMock.setCalle("Av. Siempreviva");
        direccionMock.setNumero("742");
        direccionMock.setComuna("Springfield");
        direccionMock.setCiudad("Capital City");

        // 3. Simulamos lo que devuelve la base de datos al guardar
        Shipping shippingGuardado = new Shipping();
        shippingGuardado.setId(1L);
        shippingGuardado.setOrderId(100L);
        shippingGuardado.setAddress("Av. Siempreviva 742, Springfield, Capital City");
        shippingGuardado.setStatus("PREPARANDO");

        // Configuramos Mocks
        when(orderClient.obtenerOrdenPorId(100L)).thenReturn(ordenMock);
        when(userClient.obtenerDireccionDelUsuario(5L)).thenReturn(List.of(direccionMock));
        when(repository.save(any(Shipping.class))).thenReturn(shippingGuardado);

        // --- WHEN ---
        ShippingResponseDTO response = service.crearDespacho(request);

        // --- THEN ---
        assertNotNull(response);
        assertEquals("PREPARANDO", response.getStatus());
        assertEquals("Av. Siempreviva 742, Springfield, Capital City", response.getAddress());

        // Verificamos la triple interacción
        verify(orderClient, times(1)).obtenerOrdenPorId(100L);
        verify(userClient, times(1)).obtenerDireccionDelUsuario(5L);
        verify(repository, times(1)).save(any(Shipping.class));
    }

    @Test
    void crearDespacho_FallaPorOrdenNoPagada() {
        // --- GIVEN ---
        ShippingRequestDTO request = new ShippingRequestDTO();
        request.setOrderId(100L);
        request.setUserId(5L);

        OrderClientDTO ordenPendiente = new OrderClientDTO();
        ordenPendiente.setEstado("PENDIENTE"); // El estado prohibido

        when(orderClient.obtenerOrdenPorId(100L)).thenReturn(ordenPendiente);

        // --- WHEN & THEN ---
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            service.crearDespacho(request);
        });

        // REEMPLAZO AQUÍ: Verificamos el mensaje exacto que lanza tu código
        assertEquals("Error: No se puede despachar una orden que no esté PAGADA.", exception.getMessage());

        // Verificamos que al fallar la primera regla, NUNCA se llamó al servicio de usuarios ni a la BD
        verify(userClient, never()).obtenerDireccionDelUsuario(anyLong());
        verify(repository, never()).save(any());
    }

    @Test
    void actualizarDespacho_Exito_MarcaFechaDeEntrega() {
        // --- GIVEN ---
        Shipping envioExistente = new Shipping();
        envioExistente.setId(1L);
        envioExistente.setStatus("EN_TRANSITO");
        envioExistente.setTrackingNumber("BLX-123");
        assertNull(envioExistente.getDeliveryDate()); // Aseguramos que inicialmente no tiene fecha

        when(repository.findById(1L)).thenReturn(Optional.of(envioExistente));
        when(repository.save(any(Shipping.class))).thenReturn(envioExistente);

        // --- WHEN ---
        // El operador logístico lo marca como entregado
        ShippingResponseDTO response = service.actualizarDespacho(1L, "ENTREGADO", "BLX-123");

        // --- THEN ---
        assertEquals("ENTREGADO", response.getStatus());
        assertNotNull(response.getDeliveryDate()); // Verificamos que la regla de negocio inyectó la fecha
        verify(repository, times(1)).save(envioExistente);
    }
}
