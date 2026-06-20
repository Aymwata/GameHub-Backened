package com.gamehub.reviewservice.Controllers;

import com.gamehub.reviewservice.Models.DTOs.ResenaRequestDTO;
import com.gamehub.reviewservice.Models.Resena;
import com.gamehub.reviewservice.Service.ResenaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("/api/resenas")
@RequiredArgsConstructor

@Tag(name = "Gestion de Reseñas", description = "Endpoints para la creación, visualización y moderación de calificaciones y comentarios de productos en GameHub.")
public class ResenaController {

    private final ResenaService resenaService;

    // CREACIÓN DE RESEÑA
    @Operation(summary = "Crear una nueva reseña", description = "Permite a un usuario registrar una calificación y comentario sobre un producto específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Reseña creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de validación incorrectos (ej. calificación fuera de rango)"),
            @ApiResponse(responseCode = "404", description = "El producto o el usuario especificado no existe")
    })
    @PostMapping
    public ResponseEntity<Resena> crearResena(@Valid @RequestBody ResenaRequestDTO request) {
        return new ResponseEntity<>(resenaService.crearResena(request), HttpStatus.CREATED);
    }



    // LISTADO Y FILTRADO DE RESEÑAS
    @Operation(summary = "Listar reseñas", description = "Obtiene el catálogo de reseñas. Se puede filtrar para ver todas las reseñas de un producto en particular o todas las hechas por un usuario.")
    @ApiResponse(responseCode = "200", description = "Lista de reseñas obtenida correctamente")
    @GetMapping
    public ResponseEntity<List<Resena>> listarResenas(
            @Parameter(description = "ID del producto para filtrar sus valoraciones", example = "250") @RequestParam(required = false) Long productoId,
            @Parameter(description = "ID del usuario para ver su historial de reseñas", example = "10") @RequestParam(required = false) Long usuarioId) {
        return ResponseEntity.ok(resenaService.listarResenas(productoId, usuarioId));
    }



    // BÚSQUEDA DE RESEÑA POR ID
    @Operation(summary = "Buscar reseña por ID", description = "Obtiene los detalles completos de una reseña específica utilizando su identificador único.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reseña encontrada"),
            @ApiResponse(responseCode = "404", description = "La reseña solicitada no existe")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Resena> buscarPorId(
            @Parameter(description = "ID único de la reseña", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(resenaService.buscarPorId(id));
    }



    // ACTUALIZACIÓN DE RESEÑA
    @Operation(summary = "Actualizar reseña", description = "Permite a un usuario editar el contenido o la calificación de una reseña previamente creada.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reseña actualizada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "La reseña a actualizar no existe")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Resena> actualizarResena(
            @Parameter(description = "ID de la reseña a modificar", example = "1") @PathVariable Long id,
            @Valid @RequestBody ResenaRequestDTO request) {
        return ResponseEntity.ok(resenaService.actualizarResena(id, request));
    }



    // MODERACIÓN DE RESEÑA
    @Operation(summary = "Moderar reseña", description = "Endpoint administrativo para cambiar el estado de visibilidad de una reseña (ej. ocultarla si contiene lenguaje inapropiado).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado de la reseña actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Estado proporcionado no es válido"),
            @ApiResponse(responseCode = "404", description = "La reseña a moderar no existe")
    })
    @PutMapping("/{id}/moderar")
    public ResponseEntity<Resena> moderarResena(
            @Parameter(description = "ID de la reseña a moderar", example = "1") @PathVariable Long id,
            @Parameter(description = "Nuevo estado de la reseña (ej. APROBADO, PENDIENTE, RECHAZADO)", example = "RECHAZADO") @RequestParam String nuevoEstado) {
        return ResponseEntity.ok(resenaService.moderarResena(id, nuevoEstado));
    }
}