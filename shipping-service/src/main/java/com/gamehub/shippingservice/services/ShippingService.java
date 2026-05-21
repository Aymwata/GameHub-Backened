package com.gamehub.shippingservice.services;

import com.gamehub.shippingservice.client.OrderClient;
import com.gamehub.shippingservice.client.UserClient;
import com.gamehub.shippingservice.models.Shipping;
import com.gamehub.shippingservice.models.dto.AddressClientDTO;
import com.gamehub.shippingservice.models.dto.OrderClientDTO;
import com.gamehub.shippingservice.models.dto.ShippingRequestDTO;
import com.gamehub.shippingservice.models.dto.ShippingResponseDTO;
import com.gamehub.shippingservice.repositories.ShippingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShippingService {

    private final ShippingRepository repository;
    private final OrderClient orderClient;
    private final UserClient userClient;

    // --- 1. CREAR DESPACHO ---
    public ShippingResponseDTO crearDespacho(ShippingRequestDTO request) {
        log.info("Iniciando creación de despacho para la orden: {}", request.getOrderId());

        // Regla A: Solo despachar órdenes pagadas
        OrderClientDTO orden = orderClient.obtenerOrdenPorId(request.getOrderId());
// La nueva versión tolerante:
        if (!"PAID".equalsIgnoreCase(orden.getEstado()) && !"PAGADA".equalsIgnoreCase(orden.getEstado())) {
            throw new RuntimeException("Error: No se puede despachar una orden que no esté PAGADA o PAID.");
        }

        // Regla B: Validar que el cliente tenga dirección válida
// Regla B: Validar que el cliente tenga dirección válida (Ahora recibiendo una lista)
        List<AddressClientDTO> direcciones = userClient.obtenerDireccionDelUsuario(request.getUserId());

        if (direcciones == null || direcciones.isEmpty()) {
            throw new RuntimeException("Error: El cliente no tiene ninguna dirección válida registrada.");
        }

        // Tomamos la primera dirección de la lista por defecto
        AddressClientDTO direccion = direcciones.get(0);

        if (direccion.getCalle() == null || direccion.getCalle().isBlank()) {
            throw new RuntimeException("Error: La dirección principal del cliente está incompleta.");
        }

        // Formatear la dirección recibida por Feign
        String direccionCompleta = direccion.getCalle() + " " + direccion.getNumero() + ", " +
                direccion.getComuna() + ", " + direccion.getCiudad();
        

        Shipping envio = new Shipping();
        envio.setOrderId(request.getOrderId());
        envio.setUserId(request.getUserId());
        envio.setCarrier(request.getCarrier());
        envio.setAddress(direccionCompleta);
        envio.setStatus("PREPARANDO");
        envio.setShippingDate(LocalDateTime.now()); // Registramos cuándo entró al sistema de logística

        Shipping guardado = repository.save(envio);
        return mapToDTO(guardado);
    }

    // --- 2. ACTUALIZAR ESTADO Y TRACKING ---
    public ShippingResponseDTO actualizarDespacho(Long id, String nuevoEstado, String trackingNumber) {
        Shipping envio = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Despacho no encontrado con ID: " + id));

        // Regla C: Tracking único cuando exista
        if (trackingNumber != null && !trackingNumber.isBlank() && !trackingNumber.equals(envio.getTrackingNumber())) {
            repository.findByTrackingNumber(trackingNumber).ifPresent(s -> {
                throw new RuntimeException("Error: El número de seguimiento ya existe en otro despacho.");
            });
            envio.setTrackingNumber(trackingNumber);
        }

        // Regla D: No cambiar a entregado sin fecha de entrega [cite: 215]
        if ("ENTREGADO".equalsIgnoreCase(nuevoEstado)) {
            envio.setDeliveryDate(LocalDateTime.now()); // Aseguramos que tenga fecha de entrega al cambiar a este estado
        }

        envio.setStatus(nuevoEstado.toUpperCase());
        Shipping actualizado = repository.save(envio);
        return mapToDTO(actualizado);
    }

    // --- 3. CANCELAR DESPACHO ---
    public void cancelarDespacho(Long id) {
        Shipping envio = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Despacho no encontrado."));

        // Regla E: Cancelar despacho si la orden fue anulada [cite: 207]
        envio.setStatus("CANCELADO");
        repository.save(envio);
    }

    // --- 4. CONSULTAS (GET) ---
    public ShippingResponseDTO obtenerPorId(Long id) {
        return mapToDTO(repository.findById(id).orElseThrow(() -> new RuntimeException("Despacho no encontrado.")));
    }

    public ShippingResponseDTO obtenerPorOrden(Long orderId) {
        return mapToDTO(repository.findByOrderId(orderId).orElseThrow(() -> new RuntimeException("Sin despacho para esta orden.")));
    }

    public List<ShippingResponseDTO> obtenerPorEstado(String status) {
        return repository.findByStatus(status.toUpperCase()).stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    // --- MAPEO INTERNO ---
    private ShippingResponseDTO mapToDTO(Shipping entity) {
        ShippingResponseDTO dto = new ShippingResponseDTO();
        dto.setId(entity.getId());
        dto.setOrderId(entity.getOrderId());
        dto.setUserId(entity.getUserId());
        dto.setAddress(entity.getAddress());
        dto.setCarrier(entity.getCarrier());
        dto.setTrackingNumber(entity.getTrackingNumber());
        dto.setStatus(entity.getStatus());
        dto.setShippingDate(entity.getShippingDate());
        dto.setDeliveryDate(entity.getDeliveryDate());
        return dto;
    }
}
