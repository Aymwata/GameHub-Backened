package com.gamehub.category_service.services;

import com.gamehub.category_service.models.Categoria;
import com.gamehub.category_service.models.dto.CategoriaRequestDTO;
import com.gamehub.category_service.models.dto.CategoriaResponseDTO;
import com.gamehub.category_service.repositories.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor // Lombok inyecta el repositorio automáticamente
public class CategoriaService {

    private final CategoriaRepository repository;

    // --- MÉTODOS PRIVADOS DE TRADUCCIÓN (MAPEO) ---
    // En la defensa técnica, di: "Usamos métodos manuales de mapeo para desacoplar la entidad del controlador"
    private CategoriaResponseDTO mapToDTO(Categoria categoria) {
        return new CategoriaResponseDTO(
                categoria.getId(),
                categoria.getNombre(),
                categoria.getDescripcion(),
                categoria.getEstado()
        );
    }

    // --- LÓGICA DE NEGOCIO ---

    public List<CategoriaResponseDTO> obtenerTodas() {
        return repository.findAll()
                .stream()
                .map(this::mapToDTO) // Convierte cada Entidad en un DTO
                .collect(Collectors.toList());
    }

    public CategoriaResponseDTO obtenerPorId(Long id) {
        Categoria categoria = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
        return mapToDTO(categoria);
    }

    public CategoriaResponseDTO crearCategoria(CategoriaRequestDTO request) {
        Categoria nueva = new Categoria();
        nueva.setNombre(request.getNombre());
        nueva.setDescripcion(request.getDescripcion());
        // El estado = true ya viene por defecto en la Entidad

        Categoria guardada = repository.save(nueva);
        return mapToDTO(guardada);
    }

    public CategoriaResponseDTO actualizarCategoria(Long id, CategoriaRequestDTO request) {
        Categoria existente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        existente.setNombre(request.getNombre());
        existente.setDescripcion(request.getDescripcion());

        Categoria actualizada = repository.save(existente);
        return mapToDTO(actualizada);
    }

    public void desactivarCategoria(Long id) {
        Categoria existente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
        existente.setEstado(false);
        repository.save(existente);
    }

    // El endpoint interno para que OpenFeign valide rápido
    public boolean existeCategoria(Long id) {
        return repository.existsById(id);
    }
}