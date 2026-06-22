package com.gamehub.category_service.services;

import com.gamehub.category_service.models.Categoria;
import com.gamehub.category_service.models.dto.CategoriaRequestDTO;
import com.gamehub.category_service.models.dto.CategoriaResponseDTO;
import com.gamehub.category_service.repositories.CategoriaRepository;
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
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository repository;

    @InjectMocks
    private CategoriaService service;

    @Test
    void crearCategoria_Exito() {
        // --- GIVEN ---
        CategoriaRequestDTO request = new CategoriaRequestDTO();
        request.setNombre("Monitores");
        request.setDescripcion("Pantallas de alta tasa de refresco");

        Categoria categoriaGuardada = new Categoria();
        categoriaGuardada.setId(1L);
        categoriaGuardada.setNombre("Monitores");
        categoriaGuardada.setDescripcion("Pantallas de alta tasa de refresco");
        categoriaGuardada.setEstado(true);

        when(repository.save(any(Categoria.class))).thenReturn(categoriaGuardada);

        // --- WHEN ---
        CategoriaResponseDTO response = service.crearCategoria(request);

        // --- THEN ---
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Monitores", response.getNombre());
        assertTrue(response.getEstado()); // Verificamos que nazca activa por defecto

        verify(repository, times(1)).save(any(Categoria.class));
    }

    @Test
    void obtenerPorId_Exito() {
        // --- GIVEN ---
        Categoria categoriaEnBD = new Categoria();
        categoriaEnBD.setId(2L);
        categoriaEnBD.setNombre("Tarjetas Gráficas");
        categoriaEnBD.setEstado(true);

        when(repository.findById(2L)).thenReturn(Optional.of(categoriaEnBD));

        // --- WHEN ---
        CategoriaResponseDTO response = service.obtenerPorId(2L);

        // --- THEN ---
        assertNotNull(response);
        assertEquals("Tarjetas Gráficas", response.getNombre());
    }

    @Test
    void obtenerPorId_FallaNoEncontrado() {
        // --- GIVEN ---
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // --- WHEN & THEN ---
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            service.obtenerPorId(99L);
        });

        assertTrue(exception.getMessage().contains("no encontrad"));
    }

    @Test
    void desactivarCategoria_Exito_SoftDelete() {
        // --- GIVEN ---
        Categoria categoriaActiva = new Categoria();
        categoriaActiva.setId(5L);
        categoriaActiva.setNombre("Periféricos");
        categoriaActiva.setEstado(true); // Está activa inicialmente

        when(repository.findById(5L)).thenReturn(Optional.of(categoriaActiva));

        when(repository.save(any(Categoria.class))).thenReturn(categoriaActiva);

        // --- WHEN ---
        service.desactivarCategoria(5L);

        // --- THEN ---

        assertFalse(categoriaActiva.getEstado(), "El estado de la categoría debió cambiar a false");


        verify(repository, times(1)).save(categoriaActiva);
        verify(repository, never()).delete(any());
    }
}
