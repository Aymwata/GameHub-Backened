package com.gamehub.reviewservice.Service;

import com.gamehub.reviewservice.Models.DTOs.ResenaRequestDTO;
import com.gamehub.reviewservice.Models.DTOs.*;
import com.gamehub.reviewservice.Models.Resena;
import com.gamehub.reviewservice.Exceptions.*;
import com.gamehub.reviewservice.Client.*;
import com.gamehub.reviewservice.Repositories.ResenaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResenaService {

    private final ResenaRepository resenaRepository;
    private final UserClient userClient;
    private final ProductClient productClient;
    private final OrderClient orderClient;

    // 1. Crear reseña con validaciones cruzadas
    public Resena crearResena(ResenaRequestDTO request) {
        log.info("Iniciando validación para crear reseña. Orden: {}, Producto: {}", request.getOrdenId(), request.getProductoId());

        // Regla: No permitir múltiples reseñas para la misma compra y producto
        if (resenaRepository.existsByOrdenIdAndProductoId(request.getOrdenId(), request.getProductoId())) {
            throw new BusinessRuleException("Ya existe una reseña para este producto en esta orden.");
        }

        // Validar Usuario
        UsuarioDTO usuario;
        try {
            usuario = userClient.buscarPorId(request.getUsuarioId());
        } catch (Exception e) {
            throw new ResourceNotFoundException("Usuario no encontrado en el sistema.");
        }

        // Validar si está activo
        if (usuario.getEstado() != null && !usuario.getEstado()) {
            throw new BusinessRuleException("El usuario está inactivo.");
        }

        //Validar si el Producto existe
        try {
            productClient.obtenerProducto(request.getProductoId());
        } catch (Exception e) {
            throw new ResourceNotFoundException("Producto no encontrado en el sistema.");
        }

        //Validar si la Orden existe
        OrdenDTO orden;
        try {
            orden = orderClient.buscarPorId(request.getOrdenId());
        } catch (Exception e) {
            throw new ResourceNotFoundException("Orden no encontrada en el sistema.");
        }

        // 5. Validar pertenencia de la orden (Fuera del try-catch)
        if (!orden.getUsuarioId().equals(request.getUsuarioId())) {
            throw new BusinessRuleException("La orden no pertenece al usuario especificado.");
        }

        boolean comproProducto = orden.getDetalles().stream()
                .anyMatch(detalle -> detalle.getProductoId().equals(request.getProductoId()));

        if (!comproProducto) {
            throw new BusinessRuleException("El usuario no compró este producto en la orden especificada. Solo puede reseñar quien compró el producto.");
        }

        // Construir y guardar
        Resena resena = new Resena();
        resena.setUsuarioId(request.getUsuarioId());
        resena.setProductoId(request.getProductoId());
        resena.setOrdenId(request.getOrdenId());
        resena.setPuntuacion(request.getPuntuacion());
        resena.setComentario(request.getComentario());
        resena.setEstado("ACTIVA");

        log.info("Validaciones superadas. Guardando reseña...");
        return resenaRepository.save(resena);
    }

    // 2. Listar reseñas por producto o usuario
    public List<Resena> listarResenas(Long productoId, Long usuarioId) {
        if (productoId != null) return resenaRepository.findByProductoId(productoId);
        if (usuarioId != null) return resenaRepository.findByUsuarioId(usuarioId);
        return resenaRepository.findAll();
    }

    // 3. Buscar reseña por ID
    public Resena buscarPorId(Long id) {
        return resenaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reseña no encontrada con ID: " + id));
    }

    // 4. Actualizar comentario o calificación
    public Resena actualizarResena(Long id, ResenaRequestDTO request) {
        Resena resena = buscarPorId(id);
        resena.setPuntuacion(request.getPuntuacion());
        resena.setComentario(request.getComentario());
        return resenaRepository.save(resena);
    }

    // 5. Eliminar o moderar reseña
    public Resena moderarResena(Long id, String nuevoEstado) {
        Resena resena = buscarPorId(id);
        resena.setEstado(nuevoEstado);
        return resenaRepository.save(resena);
    }
}