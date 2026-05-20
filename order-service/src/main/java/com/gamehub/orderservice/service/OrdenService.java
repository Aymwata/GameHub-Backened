package com.gamehub.orderservice.service;

import com.gamehub.orderservice.client.*;
import com.gamehub.orderservice.model.DTOs.*;
import com.gamehub.orderservice.exceptions.OrdenException;
import com.gamehub.orderservice.model.DetalleOrden;
import com.gamehub.orderservice.model.Orden;
import com.gamehub.orderservice.repository.OrdenRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrdenService {

    private static final Logger log = LoggerFactory.getLogger(OrdenService.class);

    private final OrdenRepository ordenRepository;
    private final UsuarioClient usuarioClient;
    private final ProductoClient productoClient;
    private final InventarioClient inventarioClient;
    private final PromocionClient promocionClient;

    @Transactional
    public Orden crearOrden(OrdenRequestDTO request) {
        log.info("Iniciando creación de orden para el usuario: {}", request.getUsuarioId());

        // 1. Validar Usuario
        UsuarioClientDTO usuario = validarUsuario(request.getUsuarioId());

        Orden nuevaOrden = new Orden();
        nuevaOrden.setUsuarioId(usuario.getId());
        nuevaOrden.setEstado("PENDIENTE");
        nuevaOrden.setDetalles(new ArrayList<>());

        double subtotal = 0.0;

        // 2. Procesar Detalles, Validar Productos y Stock
        for (DetalleOrdenRequestDTO detalleReq : request.getDetalles()) {
            // Validar existencia y estado del producto
            ProductoClientDTO producto = validarProducto(detalleReq.getProductoId());

            // Validar stock disponible
            validarStock(producto.getId(), detalleReq.getCantidad());

            DetalleOrden detalle = new DetalleOrden();
            detalle.setProductoId(producto.getId());
            detalle.setCantidad(detalleReq.getCantidad());
            detalle.setPrecioUnitario(producto.getPrecio()); // Aseguramos el precio histórico
            detalle.setOrden(nuevaOrden);

            nuevaOrden.getDetalles().add(detalle);
            subtotal += (producto.getPrecio() * detalleReq.getCantidad());
        }

        nuevaOrden.setSubtotal(subtotal);

        // 3. Aplicar Promoción (Si existe)
        if (request.getCodigoPromocion() != null && !request.getCodigoPromocion().isBlank()) {
            double descuento = calcularDescuento(request.getCodigoPromocion(), subtotal);
            nuevaOrden.setDescuento(descuento);
            nuevaOrden.setCodigoPromocion(request.getCodigoPromocion());
        }

        nuevaOrden.setTotal(nuevaOrden.getSubtotal() - nuevaOrden.getDescuento());

        // 4. Reservar Stock Físico (Solo se hace si todas las validaciones anteriores pasaron)
        reservarStockEnInventario(nuevaOrden.getDetalles());

        // 5. Guardar en Base de Datos
        Orden ordenGuardada = ordenRepository.save(nuevaOrden);
        log.info("Orden creada exitosamente con ID: {}", ordenGuardada.getId());
        return ordenGuardada;
    }

    public List<Orden> listarOrdenes(Long usuarioId, String estado) {
        if (usuarioId != null) return ordenRepository.findByUsuarioId(usuarioId);
        if (estado != null) return ordenRepository.findByEstado(estado.toUpperCase());
        return ordenRepository.findAll();
    }

    public Orden buscarPorId(Long id) {
        return ordenRepository.findById(id)
                .orElseThrow(() -> new OrdenException("La orden no existe"));
    }

    public Orden actualizarEstado(Long id, String nuevoEstado) {
        Orden orden = buscarPorId(id);

        if (orden.getEstado().equals("PAGADA") || orden.getEstado().equals("CANCELADA")) {
            throw new OrdenException("No se puede modificar una orden que ya está Pagada o Cancelada");
        }

        orden.setEstado(nuevoEstado.toUpperCase());
        return ordenRepository.save(orden);
    }

    @Transactional
    public Orden cancelarOrden(Long id) {
        Orden orden = buscarPorId(id);

        if (orden.getEstado().equals("PAGADA")) {
            throw new OrdenException("No se puede cancelar una orden ya pagada");
        }

        orden.setEstado("CANCELADA");

        // Aquí deberías crear una llamada al inventory-service para LIBERAR el stock reservado.
        // Queda como mejora para implementar con un endpoint de @PatchMapping("/release") en tu inventario.

        log.info("Orden {} cancelada. (Se requiere liberar stock)", id);
        return ordenRepository.save(orden);
    }

    // --- Métodos Privados de Validación ---

    private UsuarioClientDTO validarUsuario(Long id) {
        try {
            UsuarioClientDTO usuario = usuarioClient.obtenerUsuario(id);
            if (!usuario.getEstado()) {
                throw new OrdenException("El usuario está inactivo y no puede comprar");
            }
            return usuario;
        } catch (Exception e) {
            throw new OrdenException("Error al validar el usuario: No existe o el servicio no responde");
        }
    }

    private ProductoClientDTO validarProducto(Long id) {
        try {
            ProductoClientDTO producto = productoClient.obtenerProducto(id);
            if (!producto.getEstado()) {
                throw new OrdenException("El producto ID " + id + " está inactivo y no puede venderse");
            }
            return producto;
        } catch (Exception e) {
            throw new OrdenException("Error al validar el producto ID " + id);
        }
    }

    private void validarStock(Long productoId, Integer cantidadRequerida) {
        try {
            InventarioClientDTO inventario = inventarioClient.consultarStock(productoId);
            if (inventario.getActualAvailable() < cantidadRequerida) {
                throw new OrdenException("Stock insuficiente para el producto ID " + productoId);
            }
        } catch (OrdenException oe) {
            throw oe; // Relanzar nuestra excepción
        } catch (Exception e) {
            throw new OrdenException("Error al consultar el stock del producto ID " + productoId);
        }
    }

    private double calcularDescuento(String codigo, double subtotal) {
        try {
            PromocionClientDTO promo = promocionClient.obtenerPorCodigo(codigo);
            LocalDateTime ahora = LocalDateTime.now();

            if (!promo.getEstado() || ahora.isBefore(promo.getFechaInicio()) || ahora.isAfter(promo.getFechaFin())) {
                throw new OrdenException("El código promocional no está vigente");
            }
            if (promo.getMontoMinimo() != null && subtotal < promo.getMontoMinimo()) {
                throw new OrdenException("El subtotal no alcanza el monto mínimo para esta promoción");
            }

            double descuentoCalculado = 0.0;
            if (promo.getTipo().equalsIgnoreCase("PORCENTAJE")) {
                descuentoCalculado = subtotal * (promo.getValor() / 100.0);
            } else if (promo.getTipo().equalsIgnoreCase("MONTO_FIJO")) {
                descuentoCalculado = promo.getValor();
            }

            // El descuento no puede ser mayor al subtotal
            return Math.min(descuentoCalculado, subtotal);

        } catch (OrdenException oe) {
            throw oe;
        } catch (Exception e) {
            throw new OrdenException("Error al validar el código promocional: No existe o no es válido");
        }
    }

    private void reservarStockEnInventario(List<DetalleOrden> detalles) {
        for (DetalleOrden detalle : detalles) {
            try {
                inventarioClient.reservarStock(detalle.getProductoId(), detalle.getCantidad());
            } catch (Exception e) {
                throw new OrdenException("Fallo crítico al intentar reservar el stock en el inventario");
            }
        }
    }
}
