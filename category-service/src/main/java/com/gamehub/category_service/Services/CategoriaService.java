package com.gamehub.category_service.Services;

import com.gamehub.category_service.Models.Categoria;
import com.gamehub.category_service.Repositories.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor // Lombok inyecta el repositorio automáticamente
public class CategoriaService {

    private final CategoriaRepository repository;

    public List<Categoria> obtenerTodas() {
        return repository.findAll();
    }

    public Categoria obtenerPorId(Long id) {
        // Retorna la categoría o lanza una excepción si no existe
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + id));
    }

    public Categoria crearCategoria(Categoria categoria) {
        categoria.setEstado(true); // Regla de negocio: toda categoría nueva nace activa
        return repository.save(categoria);
    }

    public Categoria actualizarCategoria(Long id, Categoria datosNuevos) {
        Categoria existente = obtenerPorId(id);
        existente.setNombre(datosNuevos.getNombre());
        existente.setDescripcion(datosNuevos.getDescripcion());
        // No actualizamos el ID ni el estado aquí por seguridad
        return repository.save(existente);
    }

    public void desactivarCategoria(Long id) {
        Categoria existente = obtenerPorId(id);
        existente.setEstado(false); // Regla de negocio: Desactivación lógica, no eliminación física
        repository.save(existente);
    }
}