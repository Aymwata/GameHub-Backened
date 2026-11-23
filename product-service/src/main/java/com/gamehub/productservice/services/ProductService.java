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
import java.util.LinkedHashMap;
import java.util.Map;
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
        dto.setImagen(producto.getImagen());
        dto.setDetallesTecnicos(producto.getDetallesTecnicos());
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
        nuevo.setImagen(request.getImagen());
        nuevo.setDetallesTecnicos(normalizarDetallesTecnicos(request.getDetallesTecnicos()));
        nuevo.setPrecio(request.getPrecio());
        nuevo.setCategoriaId(request.getCategoriaId());


        Product guardado = repository.save(nuevo);
        return mapToDTO(guardado);
    }

    public ProductoResponseDTO actualizarDetallesTecnicos(Long id, Map<String, String> detallesTecnicos) {
        Product producto = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
        producto.setDetallesTecnicos(normalizarDetallesTecnicos(detallesTecnicos));
        return mapToDTO(repository.save(producto));
    }

    private Map<String, String> normalizarDetallesTecnicos(Map<String, String> detallesTecnicos) {
        Map<String, String> normalizados = new LinkedHashMap<>();
        if (detallesTecnicos == null) return normalizados;
        detallesTecnicos.forEach((caracteristica, detalle) -> {
            if (caracteristica == null || detalle == null) return;
            String clave = caracteristica.trim();
            String valor = detalle.trim();
            if (!clave.isEmpty() && !valor.isEmpty()) normalizados.put(clave, valor);
        });
        return normalizados;
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
