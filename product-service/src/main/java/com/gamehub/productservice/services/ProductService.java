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
    private final CategoryClient categoryClient;


    private ProductoResponseDTO mapToDTO(Product producto) {
        ProductoResponseDTO dto = new ProductoResponseDTO();
        dto.setId(producto.getId());
        dto.setNombre(producto.getNombre());
        dto.setMarca(producto.getMarca());
        dto.setModelo(producto.getModelo());
        dto.setDescripcion(producto.getDescripcion());
        dto.setPrecio(producto.getPrecio());

        dto.setEstado(producto.getEstado());

        CategoriaClientDTO categoriaDTO = categoryClient.obtenerCategoriaPorId(producto.getCategoriaId());
        dto.setCategoria(categoriaDTO);

        return dto;
    }



    public ProductoResponseDTO crearProducto(ProductoRequestDTO request) {

        boolean categoriaExiste = categoryClient.verificarExistencia(request.getCategoriaId());

        if (!categoriaExiste) {
            throw new RuntimeException("Error: La categoría con ID " + request.getCategoriaId() + " no existe.");
        }

        Product nuevo = new Product();
        nuevo.setNombre(request.getNombre());
        nuevo.setMarca(request.getMarca());
        nuevo.setModelo(request.getModelo());
        nuevo.setDescripcion(request.getDescripcion());
        nuevo.setPrecio(request.getPrecio());
        nuevo.setCategoriaId(request.getCategoriaId());


        Product guardado = repository.save(nuevo);
        return mapToDTO(guardado);
    }

    public List<ProductoResponseDTO> obtenerTodos() {
        return repository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public ProductoResponseDTO obtenerPorId(Long id) {
        Product producto = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
        return mapToDTO(producto);
    }
}
