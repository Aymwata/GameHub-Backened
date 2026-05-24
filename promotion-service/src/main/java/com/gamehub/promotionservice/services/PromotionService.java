package com.gamehub.promotionservice.services;

import com.gamehub.promotionservice.client.CategoryClient;
import com.gamehub.promotionservice.client.ProductClient;
import com.gamehub.promotionservice.models.Promotion;
import com.gamehub.promotionservice.models.dto.CategoriaClientDTO;
import com.gamehub.promotionservice.models.dto.ProductoClientDTO;
import com.gamehub.promotionservice.models.dto.PromotionRequestDTO;
import com.gamehub.promotionservice.models.dto.PromotionResponseDTO;
import com.gamehub.promotionservice.repositories.PromotionRepository; // Asegúrate de que este paquete coincida con tu estructura
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromotionService {

    private final PromotionRepository repository;
    private final CategoryClient categoryClient;
    private final ProductClient productClient;



    public PromotionResponseDTO crearPromocion(PromotionRequestDTO request) {

        if (request.getFechaInicio().isAfter(request.getFechaFin())) {
            throw new RuntimeException("Error: La fecha de inicio no puede ser posterior a la fecha de fin.");
        }


        if (repository.findByCodigo(request.getCodigo()).isPresent()) {
            throw new RuntimeException("Error: El código de promoción ya existe.");
        }


        if (request.getProductoId() != null) {
            productClient.obtenerProducto(request.getProductoId());
        }
        if (request.getCategoriaId() != null) {
            categoryClient.obtenerCategoria(request.getCategoriaId());
        }


        Promotion nueva = new Promotion();
        nueva.setCodigo(request.getCodigo());
        nueva.setTipo(request.getTipo());
        nueva.setValor(request.getValor());
        nueva.setFechaInicio(request.getFechaInicio());
        nueva.setFechaFin(request.getFechaFin());
        nueva.setMontoMinimo(request.getMontoMinimo());
        nueva.setUsosMaximos(request.getUsosMaximos());
        nueva.setProductoId(request.getProductoId());
        nueva.setCategoriaId(request.getCategoriaId());


        Promotion guardada = repository.save(nueva);
        return mapToDTO(guardada);
    }

    public List<PromotionResponseDTO> obtenerTodas() {
        return repository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public PromotionResponseDTO obtenerPorId(Long id) {
        Promotion promocion = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Promoción no encontrada con ID: " + id));
        return mapToDTO(promocion);
    }



    private PromotionResponseDTO mapToDTO(Promotion promocion) {
        PromotionResponseDTO dto = new PromotionResponseDTO();
        dto.setId(promocion.getId());
        dto.setCodigo(promocion.getCodigo());
        dto.setTipo(promocion.getTipo());
        dto.setValor(promocion.getValor());
        dto.setFechaInicio(promocion.getFechaInicio());
        dto.setFechaFin(promocion.getFechaFin());
        dto.setMontoMinimo(promocion.getMontoMinimo());
        dto.setUsosMaximos(promocion.getUsosMaximos());
        dto.setEstado(promocion.getEstado());


        if (promocion.getProductoId() != null) {
            try {
                ProductoClientDTO producto = productClient.obtenerProducto(promocion.getProductoId());
                dto.setProducto(producto);
            } catch (Exception e) {
                dto.setProducto(null);
            }
        }

        if (promocion.getCategoriaId() != null) {
            try {
                CategoriaClientDTO categoria = categoryClient.obtenerCategoria(promocion.getCategoriaId());
                dto.setCategoria(categoria);
            } catch (Exception e) {
                dto.setCategoria(null);
            }
        }

        return dto;
    }


    public PromotionResponseDTO obtenerPorCodigo(String codigo) {
        Promotion promotion = repository.findByCodigo(codigo)
                .orElseThrow(() -> new RuntimeException("Promoción no encontrada con código: " + codigo));


        if (!promotion.getEstado()) {
            throw new RuntimeException("Error: Este código de promoción se encuentra inactivo.");
        }

        return mapToDTO(promotion);
    }


    public PromotionResponseDTO actualizarPromocion(Long id, PromotionRequestDTO request) {
        Promotion existente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Promoción no encontrada con ID: " + id));


        if (request.getFechaInicio().isAfter(request.getFechaFin())) {
            throw new RuntimeException("Error: La nueva fecha de inicio no puede ser posterior a la fecha de fin.");
        }


        repository.findByCodigo(request.getCodigo()).ifPresent(promo -> {
            if (!promo.getId().equals(id)) {
                throw new RuntimeException("Error: El nuevo código de promoción ya está en uso por otro cupón.");
            }
        });


        if (request.getProductoId() != null) {
            productClient.obtenerProducto(request.getProductoId());
        }
        if (request.getCategoriaId() != null) {
            categoryClient.obtenerCategoria(request.getCategoriaId());
        }


        existente.setCodigo(request.getCodigo());
        existente.setTipo(request.getTipo());
        existente.setValor(request.getValor());
        existente.setFechaInicio(request.getFechaInicio());
        existente.setFechaFin(request.getFechaFin());
        existente.setMontoMinimo(request.getMontoMinimo());
        existente.setUsosMaximos(request.getUsosMaximos());
        existente.setProductoId(request.getProductoId());
        existente.setCategoriaId(request.getCategoriaId());

        Promotion guardada = repository.save(existente);
        return mapToDTO(guardada);
    }


    public void desactivarPromocion(Long id) {
        Promotion existente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Promoción no encontrada con ID: " + id));

        existente.setEstado(false);
        repository.save(existente);
    }
}