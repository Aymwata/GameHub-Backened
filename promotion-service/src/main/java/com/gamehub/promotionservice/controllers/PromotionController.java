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
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/promotions")
@RequiredArgsConstructor
@Tag(name = "Motor de Promociones", description = "Endpoints de GameHub para la creación, validación y gestión de cupones de descuento")
public class PromotionController {

    private final PromotionService service;

    @PostMapping
    @Operation(summary = "Crear una nueva promoción", description = "Registra un nuevo cupón de descuento con su porcentaje y estado activo en el sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Promoción creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o error de validación", content = @Content)
    })
    public ResponseEntity<EntityModel<PromotionResponseDTO>> crearPromocion(@Valid @RequestBody PromotionRequestDTO request) {
        PromotionResponseDTO response = service.crearPromocion(request);

        EntityModel<PromotionResponseDTO> resource = EntityModel.of(response,
                linkTo(methodOn(PromotionController.class).obtenerPorId(response.getId())).withSelfRel(),
                linkTo(methodOn(PromotionController.class).obtenerTodas()).withRel("todas-las-promociones"));

        return ResponseEntity.status(HttpStatus.CREATED).body(resource);
    }

    @GetMapping
    @Operation(summary = "Obtener todas las promociones", description = "Retorna una lista completa con todos los cupones configurados en la plataforma.")
    public ResponseEntity<CollectionModel<EntityModel<PromotionResponseDTO>>> obtenerTodas() {
        List<EntityModel<PromotionResponseDTO>> promociones = service.obtenerTodas().stream()
                .map(promo -> EntityModel.of(promo,
                        linkTo(methodOn(PromotionController.class).obtenerPorId(promo.getId())).withSelfRel()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(promociones,
                linkTo(methodOn(PromotionController.class).obtenerTodas()).withSelfRel()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar promoción por ID", description = "Busca una promoción específica mediante su identificador único numérico.")
    public ResponseEntity<EntityModel<PromotionResponseDTO>> obtenerPorId(@Parameter(description = "ID único", example = "1") @PathVariable Long id) {
        PromotionResponseDTO response = service.obtenerPorId(id);

        EntityModel<PromotionResponseDTO> resource = EntityModel.of(response,
                linkTo(methodOn(PromotionController.class).obtenerPorId(id)).withSelfRel(),
                linkTo(methodOn(PromotionController.class).obtenerTodas()).withRel("todas-las-promociones"));

        return ResponseEntity.ok(resource);
    }

    @GetMapping("/codigo/{codigo}")
    @Operation(summary = "Buscar promoción por código", description = "Busca y valida una promoción utilizando el texto del código promocional.")
    public ResponseEntity<EntityModel<PromotionResponseDTO>> obtenerPorCodigo(@Parameter(description = "Cupón", example = "GAMER20") @PathVariable String codigo) {
        PromotionResponseDTO response = service.obtenerPorCodigo(codigo);

        EntityModel<PromotionResponseDTO> resource = EntityModel.of(response,
                linkTo(methodOn(PromotionController.class).obtenerPorCodigo(codigo)).withSelfRel(),
                linkTo(methodOn(PromotionController.class).obtenerTodas()).withRel("todas-las-promociones"));

        return ResponseEntity.ok(resource);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar promoción existente")
    public ResponseEntity<EntityModel<PromotionResponseDTO>> actualizarPromocion(@PathVariable Long id, @Valid @RequestBody PromotionRequestDTO request) {
        PromotionResponseDTO response = service.actualizarPromocion(id, request);

        EntityModel<PromotionResponseDTO> resource = EntityModel.of(response,
                linkTo(methodOn(PromotionController.class).obtenerPorId(id)).withSelfRel(),
                linkTo(methodOn(PromotionController.class).obtenerTodas()).withRel("todas-las-promociones"));

        return ResponseEntity.ok(resource);
    }

    @PatchMapping("/{id}/desactivar")
    @Operation(summary = "Desactivar un cupón", description = "Realiza una baja lógica del cupón.")
    public ResponseEntity<Void> desactivarPromocion(@PathVariable Long id) {
        service.desactivarPromocion(id);
        return ResponseEntity.noContent().build();
    }
}