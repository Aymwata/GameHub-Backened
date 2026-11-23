package com.gamehub.productservice.services;

import com.gamehub.productservice.client.CategoryClient;
import com.gamehub.productservice.models.Product;
import com.gamehub.productservice.models.dto.CategoriaClientDTO;
import com.gamehub.productservice.models.dto.ProductoRequestDTO;
import com.gamehub.productservice.models.dto.ProductoResponseDTO;
import com.gamehub.productservice.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
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
        dto.setEspecificaciones(producto.getEspecificaciones());
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
        nuevo.setEspecificaciones(normalizarEspecificaciones(request.getEspecificaciones()));
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
        return mapToDTO(buscarProducto(id));
    }

    @Transactional
    public void desactivarProducto(Long id) {
        buscarProducto(id).setEstado(false);
    }

    @Transactional
    public ProductoResponseDTO actualizarEspecificaciones(Long id, Map<String, String> especificaciones) {
        Product producto = buscarProducto(id);
        producto.setEspecificaciones(normalizarEspecificaciones(especificaciones));
        return mapToDTO(producto);
    }

    private Map<String, String> normalizarEspecificaciones(Map<String, String> especificaciones) {
        if (especificaciones == null) return null;
        Map<String, String> resultado = new LinkedHashMap<>();
        especificaciones.forEach((nombre, valor) -> resultado.put(nombre.strip(), valor.strip()));
        return resultado;
    }

    private Product buscarProducto(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
    }
}
