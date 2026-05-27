// Lógica de negocio: Verifica que la orden exista, compara montos y aprueba el cobro.
package com.gamehub.paymentservice.services;

import com.gamehub.paymentservice.client.OrderClient;
import com.gamehub.paymentservice.models.Pago;
import com.gamehub.paymentservice.models.dto.*;
import com.gamehub.paymentservice.repositories.PagoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PagoService {

    private final PagoRepository pagoRepository;
    private final OrderClient orderClient;

    @Transactional
    public PagoResponseDTO procesarPago(PagoRequestDTO dto) {
        log.info("Iniciando procesamiento de pago para la Orden ID: {}", dto.getOrdenId());

        OrdenClientDTO orden;
        try {
            orden = orderClient.getOrdenById(dto.getOrdenId());
        } catch (Exception e) {
            log.error("Error al validar existencia de la orden {}: {}", dto.getOrdenId(), e.getMessage());
            throw new RuntimeException("Violación de integridad: La orden no existe en el catálogo maestro.");
        }

        if (pagoRepository.existsByOrdenIdAndEstado(dto.getOrdenId(), "APPROVED")) {
            log.warn("Intento de pago duplicado rechazado para la orden: {}", dto.getOrdenId());
            throw new RuntimeException("Operación fallida: Esta orden ya cuenta con un pago aprobado.");
        }

        if (!orden.getTotal().equals(dto.getMonto())) {
            log.warn("Monto inválido. Orden requiere: {}, Recibido: {}", orden.getTotal(), dto.getMonto());
            throw new RuntimeException("Operación fallida: El monto no coincide con el total de la orden.");
        }

        Pago pago = Pago.builder()
                .ordenId(dto.getOrdenId())
                .monto(dto.getMonto())
                .metodo(dto.getMetodo().toUpperCase())
                .estado("APPROVED")
                .codigoTransaccion("TX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .usuarioId(orden.getUsuarioId())
                .build();

        Pago pagoGuardado = pagoRepository.save(pago);
        log.info("Pago aprobado con código de transacción: {}", pagoGuardado.getCodigoTransaccion());

        try {
            orderClient.actualizarEstadoOrden(orden.getId(), "PAID");
        } catch (Exception e) {
            log.error("No se pudo actualizar el estado de la orden en el servicio externo: {}", e.getMessage());
        }

        return convertToResponseDTO(pagoGuardado);
    }

    public List<PagoResponseDTO> listarPagos(Long ordenId, Long clienteId, String estado) {
        List<Pago> pagos;
        if (ordenId != null) pagos = pagoRepository.findByOrdenId(ordenId);
        else if (clienteId != null) pagos = pagoRepository.findByUsuarioId(clienteId);
        else if (estado != null) pagos = pagoRepository.findByEstado(estado.toUpperCase());
        else pagos = pagoRepository.findAll();

        return pagos.stream().map(this::convertToResponseDTO).collect(Collectors.toList());
    }

    public PagoResponseDTO buscarPorId(Long id) {
        Pago pago = pagoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Error: Registro de pago inexistente."));
        return convertToResponseDTO(pago);
    }

    @Transactional
    public PagoResponseDTO anularPago(Long id) {
        log.info("Solicitud de anulación para el pago ID: {}", id);
        Pago pago = pagoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Error: Registro de pago inexistente."));

        if ("ANULADO".equals(pago.getEstado())) {
            throw new RuntimeException("Operación fallida: El pago ya se encuentra anulado.");
        }

        pago.setEstado("ANULADO");
        Pago pagoActualizado = pagoRepository.save(pago);
        log.info("Pago ID {} anulado exitosamente.", id);

        try {
            orderClient.actualizarEstadoOrden(pago.getOrdenId(), "CANCELED");
        } catch (Exception e) {
            log.error("No se pudo notificar la anulación al order-service: {}", e.getMessage());
        }

        return convertToResponseDTO(pagoActualizado);
    }

    private PagoResponseDTO convertToResponseDTO(Pago pago) {
        PagoResponseDTO response = new PagoResponseDTO();
        response.setId(pago.getId());
        response.setOrdenId(pago.getOrdenId());
        response.setMonto(pago.getMonto());
        response.setMetodo(pago.getMetodo());
        response.setEstado(pago.getEstado());
        response.setCodigoTransaccion(pago.getCodigoTransaccion());
        response.setUsuarioId(pago.getUsuarioId());
        response.setCreatedAt(pago.getCreatedAt());
        return response;
    }
}