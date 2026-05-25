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
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository repository;

    // Mapeo para no usar los modelos reales
    private CategoriaResponseDTO mapToDTO(Categoria categoria) {
        return new CategoriaResponseDTO(
                categoria.getId(),
                categoria.getNombre(),
                categoria.getDescripcion(),
                categoria.getEstado()
        );
    }

    public List<CategoriaResponseDTO> obtenerTodas() {
        return repository.findAll()
                .stream()
                .map(this::mapToDTO)
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


    public boolean existeCategoria(Long id) {
        return repository.existsById(id);
    }
}