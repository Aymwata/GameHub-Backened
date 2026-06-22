package com.gamehub.productservice.controllers;

import com.gamehub.productservice.models.dto.ProductoRequestDTO;
import com.gamehub.productservice.models.dto.ProductoResponseDTO;
import com.gamehub.productservice.services.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Catálogo de Productos", description = "Endpoints para la gestión del catálogo de hardware y componentes de GameHub Store")
public class ProductController {

    private final ProductService service;

    @Operation(summary = "Registrar un nuevo producto", description = "Crea un producto en la base de datos tras validar la existencia de su categoría de forma síncrona.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Producto creado exitosamente",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ProductoResponseDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Error de validación en los datos enviados", content = @Content),
            @ApiResponse(responseCode = "404", description = "La categoría asignada no existe", content = @Content)
    })
    @PostMapping
    public ResponseEntity<ProductoResponseDTO> crearProducto(@Valid @RequestBody ProductoRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crearProducto(request));
    }

    @Operation(summary = "Listar todos los productos", description = "Retorna el catálogo completo de productos registrados en el sistema.")
    @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente")
    @GetMapping
    public ResponseEntity<List<ProductoResponseDTO>> obtenerTodos() {
        return ResponseEntity.ok(service.obtenerTodos());
    }

    @Operation(summary = "Buscar producto por ID", description = "Busca un producto específico y retorna sus detalles junto con la información legible de su categoría asociada.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto encontrado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado en la base de datos", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> obtenerProducto(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }
}
