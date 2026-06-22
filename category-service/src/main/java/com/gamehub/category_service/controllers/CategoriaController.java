package com.gamehub.category_service.controllers;

import com.gamehub.category_service.models.dto.CategoriaRequestDTO;
import com.gamehub.category_service.models.dto.CategoriaResponseDTO;
import com.gamehub.category_service.services.CategoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
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
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Taxonomía de Categorías", description = "Endpoints para la gestión, clasificación y estructura principal del catálogo de productos.")
public class CategoriaController {

    private final CategoriaService service;

    @Operation(summary = "Listar todas las categorías", description = "Obtiene el listado completo de categorías registradas en la base de datos.")
    @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente")
    @GetMapping
    public ResponseEntity<List<CategoriaResponseDTO>> listarCategorias() {
        return ResponseEntity.ok(service.obtenerTodas());
    }

    @Operation(summary = "Buscar categoría por ID", description = "Obtiene los detalles de una categoría específica mediante su identificador único.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categoría encontrada"),
            @ApiResponse(responseCode = "404", description = "La categoría no existe en el sistema", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> obtenerCategoria(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @Operation(summary = "Crear una nueva categoría", description = "Registra una nueva categoría en el sistema validando que el nombre no esté duplicado.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Categoría creada con éxito"),
            @ApiResponse(responseCode = "400", description = "Error de validación en los campos enviados", content = @Content)
    })
    @PostMapping
    public ResponseEntity<CategoriaResponseDTO> crearCategoria(@Valid @RequestBody CategoriaRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crearCategoria(request));
    }

    @Operation(summary = "Actualizar categoría", description = "Modifica los datos mutables (nombre, descripción) de una categoría existente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categoría actualizada correctamente"),
            @ApiResponse(responseCode = "404", description = "La categoría a actualizar no existe", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> actualizarCategoria(
            @PathVariable Long id,
            @Valid @RequestBody CategoriaRequestDTO request) {
        return ResponseEntity.ok(service.actualizarCategoria(id, request));
    }

    @Operation(summary = "Desactivar categoría (Soft Delete)", description = "Cambia el estado de la categoría a inactivo para no perder la integridad de los productos asociados.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Categoría desactivada con éxito"),
            @ApiResponse(responseCode = "404", description = "La categoría no existe", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivarCategoria(@PathVariable Long id) {
        service.desactivarCategoria(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Verificar existencia", description = "Endpoint de consumo interno (Feign) para validar si una categoría existe y está activa.")
    @ApiResponse(responseCode = "200", description = "Retorna true o false según la existencia")
    @GetMapping("/existe/{id}")
    public ResponseEntity<Boolean> verificarExistencia(@PathVariable Long id) {
        return ResponseEntity.ok(service.existeCategoria(id));
    }
}