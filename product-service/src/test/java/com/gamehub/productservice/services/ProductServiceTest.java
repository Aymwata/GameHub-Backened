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

@ExtendWith(MockitoExtension.class) // Habilita el uso de Mockito en JUnit 5
class ProductServiceTest {

    @Mock
    private ProductRepository repository; // Simulamos la base de datos

    @Mock
    private CategoryClient categoryClient; // Simulamos la red externa (Feign)

    @InjectMocks
    private ProductService service; // El servicio REAL que vamos a poner a prueba

    @Test
    void crearProducto_Exito() {
        // --- GIVEN (Dado un contexto inicial) ---
        ProductoRequestDTO request = new ProductoRequestDTO();
        request.setNombre("RTX 5090");
        request.setMarca("NVIDIA");
        request.setModelo("Founders Edition");
        request.setPrecio(1500.0); // Usamos double como acordamos para evitar fallos futuros
        request.setCategoriaId(1L);

        Product productoGuardado = new Product();
        productoGuardado.setId(1L);
        productoGuardado.setNombre("RTX 5090");
        productoGuardado.setPrecio(1500.0);
        productoGuardado.setCategoriaId(1L);

        CategoriaClientDTO categoriaFalsa = new CategoriaClientDTO();
        categoriaFalsa.setId(1L);
        categoriaFalsa.setNombre("Tarjetas Gráficas");

        // Configuramos los Mocks: "Cuando el servicio llame a esto, responde esto"
        when(categoryClient.verificarExistencia(1L)).thenReturn(true);
        when(repository.save(any(Product.class))).thenReturn(productoGuardado);
        when(categoryClient.obtenerCategoriaPorId(1L)).thenReturn(categoriaFalsa);

        // --- WHEN (Cuando ejecuto la acción) ---
        ProductoResponseDTO response = service.crearProducto(request);

        // --- THEN (Entonces verifico que el resultado sea el esperado) ---
        assertNotNull(response);
        assertEquals("RTX 5090", response.getNombre());
        assertEquals("Tarjetas Gráficas", response.getCategoria().getNombre());

        // Verificamos que el repositorio y Feign realmente fueron llamados exactamente 1 vez
        verify(categoryClient, times(1)).verificarExistencia(1L);
        verify(repository, times(1)).save(any(Product.class));
    }

    @Test
    void crearProducto_FallaPorCategoriaInexistente() {
        // --- GIVEN ---
        ProductoRequestDTO request = new ProductoRequestDTO();
        request.setCategoriaId(99L);

        // El mock ahora miente y dice que la categoría NO existe
        when(categoryClient.verificarExistencia(99L)).thenReturn(false);

        // --- WHEN & THEN ---
        // Verificamos que al intentar crear el producto, lance una RuntimeException
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            service.crearProducto(request);
        });

        // Verificamos que el mensaje de la regla de negocio sea exacto
        assertEquals("Error: La categoría con ID 99 no existe.", exception.getMessage());

        // Verificamos que la base de datos NUNCA intentó guardar (porque explotó antes)
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
        when(repository.findById(88L)).thenReturn(Optional.empty()); // La BD responde vacío

        // --- WHEN & THEN ---
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            service.obtenerPorId(88L);
        });

        assertEquals("Producto no encontrado con ID: 88", exception.getMessage());
    }
}
