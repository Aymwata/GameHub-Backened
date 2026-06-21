// Expone las rutas para que los clientes apliquen cupones de descuento en sus compras.
package com.gamehub.promotionservice.controllers;

import com.gamehub.promotionservice.models.dto.PromotionRequestDTO;
import com.gamehub.promotionservice.models.dto.PromotionResponseDTO;
import com.gamehub.promotionservice.services.PromotionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("/api/promotions")
@RequiredArgsConstructor
@Tag(name = "Motor de Promociones", description = "Endpoints de GameHub para la creación, validación y gestión de cupones de descuento")
public class PromotionController {

    private final PromotionService service;

    @PostMapping
    @Operation(summary = "Crear una nueva promoción", description = "Registra un nuevo cupón de descuento con su porcentaje y estado activo en el sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Promoción creada exitosamente",
                    content = @Content(schema = @Schema(implementation = PromotionResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o error de validación", content = @Content)
    })
    public ResponseEntity<PromotionResponseDTO> crearPromocion(@Valid @RequestBody PromotionRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crearPromocion(request));
    }

    @GetMapping
    @Operation(summary = "Obtener todas las promociones", description = "Retorna una lista completa con todos los cupones configurados en la plataforma.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de promociones recuperada con éxito")
    })
    public ResponseEntity<List<PromotionResponseDTO>> obtenerTodas() {
        return ResponseEntity.ok(service.obtenerTodas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar promoción por ID", description = "Busca una promoción específica mediante su identificador único numérico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Promoción encontrada con éxito",
                    content = @Content(schema = @Schema(implementation = PromotionResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "No se encontró ninguna promoción con el ID proporcionado", content = @Content)
    })
    public ResponseEntity<PromotionResponseDTO> obtenerPorId(
            @Parameter(description = "ID único de la promoción", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @GetMapping("/codigo/{codigo}")
    @Operation(summary = "Buscar promoción por su código de cupón", description = "Busca y valida una promoción utilizando el texto del código promocional (Ej: GAMER20).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Código promocional válido y encontrado",
                    content = @Content(schema = @Schema(implementation = PromotionResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "El código ingresado no existe o no está vigente", content = @Content)
    })
    public ResponseEntity<PromotionResponseDTO> obtenerPorCodigo(
            @Parameter(description = "Texto del cupón de descuento", example = "GAMER20")
            @PathVariable String codigo) {
        return ResponseEntity.ok(service.obtenerPorCodigo(codigo));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una promoción existente", description = "Modifica los datos (como porcentaje o nombre) de una promoción identificada por su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Promoción actualizada exitosamente",
                    content = @Content(schema = @Schema(implementation = PromotionResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos de actualización inválidos", content = @Content),
            @ApiResponse(responseCode = "404", description = "La promoción a actualizar no fue encontrada", content = @Content)
    })
    public ResponseEntity<PromotionResponseDTO> actualizarPromocion(
            @Parameter(description = "ID de la promoción a modificar", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody PromotionRequestDTO request) {
        return ResponseEntity.ok(service.actualizarPromocion(id, request));
    }

    @PatchMapping("/{id}/desactivar")
    @Operation(summary = "Desactivar un cupón", description = "Realiza una baja lógica del cupón (cambia su estado a inactivo) utilizando su ID para que no pueda seguir aplicándose.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Promoción desactivada exitosamente (Sin contenido de retorno)"),
            @ApiResponse(responseCode = "404", description = "No se encontró la promoción solicitada", content = @Content)
    })
    public ResponseEntity<Void> desactivarPromocion(
            @Parameter(description = "ID de la promoción a dar de baja", example = "1")
            @PathVariable Long id) {
        service.desactivarPromocion(id);
        return ResponseEntity.noContent().build();
    }
}