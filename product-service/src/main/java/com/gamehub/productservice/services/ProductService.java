package com.gamehub.productservice.services;

import com.gamehub.productservice.client.CategoryClient;
import com.gamehub.productservice.models.Product;
import com.gamehub.productservice.models.dto.CategoriaClientDTO;
import com.gamehub.productservice.models.dto.ProductoRequestDTO;
import com.gamehub.productservice.models.dto.ProductoResponseDTO;
import com.gamehub.productservice.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repository;
    private final CategoryClient categoryClient; // Inyectamos nuestro "teléfono" Feign

    // --- MÉTODOS PRIVADOS DE TRADUCCIÓN ---

    private ProductoResponseDTO mapToDTO(Product producto) {
        ProductoResponseDTO dto = new ProductoResponseDTO();
        dto.setId(producto.getId());
        dto.setNombre(producto.getNombre());
        dto.setDescripcion(producto.getDescripcion());
        dto.setPrecio(producto.getPrecio());
        dto.setStock(producto.getStock());

        // Llamamos al category-service para obtener los datos reales de la categoría
        // En un entorno de producción, esto requeriría manejo de Circuit Breaker (ej. Resilience4j)
        // por si el category-service está caído, pero para esta fase basta con la llamada directa.
        CategoriaClientDTO categoriaDTO = categoryClient.obtenerCategoriaPorId(producto.getCategoriaId());
        dto.setCategoria(categoriaDTO);

        return dto;
    }

    // --- LÓGICA DE NEGOCIO ---

    public ProductoResponseDTO crearProducto(ProductoRequestDTO request) {
        // REGLA DE NEGOCIO CLAVE: Validar que la categoría exista en el OTRO microservicio
        boolean categoriaExiste = categoryClient.verificarExistencia(request.getCategoriaId());

        if (!categoriaExiste) {
            throw new RuntimeException("Error: La categoría con ID " + request.getCategoriaId() + " no existe.");
        }

        Product nuevo = new Product();
        nuevo.setNombre(request.getNombre());
        nuevo.setDescripcion(request.getDescripcion());
        nuevo.setPrecio(request.getPrecio());
        nuevo.setStock(request.getStock());
        nuevo.setCategoriaId(request.getCategoriaId());
        // estado = true viene por defecto

        Product guardado = repository.save(nuevo);
        return mapToDTO(guardado);
    }

    public List<ProductoResponseDTO> obtenerTodos() {
        return repository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
}
