package com.gamehub.productservice.services;

import com.gamehub.productservice.client.CategoryClient;
import com.gamehub.productservice.models.Product;
import com.gamehub.productservice.models.dto.CategoriaClientDTO;
import com.gamehub.productservice.models.dto.ProductoRequestDTO;
import com.gamehub.productservice.models.dto.ProductoResponseDTO;
import com.gamehub.productservice.repositories.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository repository;

    @Mock
    private CategoryClient categoryClient;

    @InjectMocks
    private ProductService service;

    @Test
    void crearProducto_Exito() {
        // --- GIVEN  ---
        ProductoRequestDTO request = new ProductoRequestDTO();
        request.setNombre("RTX 5090");
        request.setMarca("NVIDIA");
        request.setModelo("Founders Edition");
        request.setPrecio(1500.0);
        request.setCategoriaId(1L);

        Product productoGuardado = new Product();
        productoGuardado.setId(1L);
        productoGuardado.setNombre("RTX 5090");
        productoGuardado.setPrecio(1500.0);
        productoGuardado.setCategoriaId(1L);

        CategoriaClientDTO categoriaFalsa = new CategoriaClientDTO();
        categoriaFalsa.setId(1L);
        categoriaFalsa.setNombre("Tarjetas Gráficas");


        when(categoryClient.verificarExistencia(1L)).thenReturn(true);
        when(repository.save(any(Product.class))).thenReturn(productoGuardado);
        when(categoryClient.obtenerCategoriaPorId(1L)).thenReturn(categoriaFalsa);

        // --- WHEN ---
        ProductoResponseDTO response = service.crearProducto(request);

        // --- THEN ---
        assertNotNull(response);
        assertEquals("RTX 5090", response.getNombre());
        assertEquals("Tarjetas Gráficas", response.getCategoria().getNombre());

        verify(categoryClient, times(1)).verificarExistencia(1L);
        verify(repository, times(1)).save(any(Product.class));
    }

    @Test
    void crearProducto_FallaPorCategoriaInexistente() {
        // --- GIVEN ---
        ProductoRequestDTO request = new ProductoRequestDTO();
        request.setCategoriaId(99L);

        when(categoryClient.verificarExistencia(99L)).thenReturn(false);

        // --- WHEN & THEN ---
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            service.crearProducto(request);
        });

        assertEquals("Error: La categoría con ID 99 no existe.", exception.getMessage());

        verify(repository, never()).save(any(Product.class));
    }

    @Test
    void obtenerPorId_Exito() {
        // --- GIVEN ---
        Product productoEnBD = new Product();
        productoEnBD.setId(5L);
        productoEnBD.setNombre("Monitor 144Hz");
        productoEnBD.setCategoriaId(2L);

        CategoriaClientDTO categoriaFalsa = new CategoriaClientDTO();
        categoriaFalsa.setNombre("Monitores");

        when(repository.findById(5L)).thenReturn(Optional.of(productoEnBD));
        when(categoryClient.obtenerCategoriaPorId(2L)).thenReturn(categoriaFalsa);

        // --- WHEN ---
        ProductoResponseDTO response = service.obtenerPorId(5L);

        // --- THEN ---
        assertNotNull(response);
        assertEquals("Monitor 144Hz", response.getNombre());
        assertEquals("Monitores", response.getCategoria().getNombre());
    }

    @Test
    void obtenerPorId_FallaPorProductoNoEncontrado() {
        // --- GIVEN ---
        when(repository.findById(88L)).thenReturn(Optional.empty());

        // --- WHEN & THEN ---
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            service.obtenerPorId(88L);
        });

        assertEquals("Producto no encontrado con ID: 88", exception.getMessage());
    }
}
