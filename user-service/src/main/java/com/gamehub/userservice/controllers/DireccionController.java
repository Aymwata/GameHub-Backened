package com.gamehub.userservice.controllers;

import com.gamehub.userservice.models.Direccion;
import com.gamehub.userservice.models.Dtos.DireccionRequestDTO;
import com.gamehub.userservice.services.DireccionService;
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
@RequestMapping("/api/directions")
@RequiredArgsConstructor

@Tag(name = "Gestion de Direcciones", description = "Operaciones para administrar las direcciones de envío o facturación asociadas a los usuarios.")
public class DireccionController {

    private final DireccionService direccionService;



    // CREACIÓN DE DIRECCIÓN
    @Operation(summary = "Crear una nueva dirección", description = "Registra una nueva dirección y la asocia a un usuario existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Dirección creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos en el formulario"),
            @ApiResponse(responseCode = "404", description = "El usuario asociado no existe")
    })
    @PostMapping
    public ResponseEntity<Direccion> crear(@Valid @RequestBody DireccionRequestDTO dto) {
        return new ResponseEntity<>(direccionService.crearDireccion(dto), HttpStatus.CREATED);
    }



    // OBTENER DIRECCIONES POR USUARIO
    @Operation(summary = "Listar direcciones de un usuario", description = "Recupera todas las direcciones registradas que pertenecen a un usuario en particular.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de direcciones recuperada"),
            @ApiResponse(responseCode = "404", description = "El usuario no existe")
    })
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Direccion>> listarPorUsuario(
            @Parameter(description = "ID del usuario dueño de las direcciones", example = "1") @PathVariable Long usuarioId) {
        return ResponseEntity.ok(direccionService.listarPorUsuario(usuarioId));
    }



    // ACTUALIZACIÓN DE DIRECCIÓN
    @Operation(summary = "Actualizar dirección", description = "Modifica los datos de una dirección específica.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dirección actualizada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "La dirección no existe")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Direccion> actualizar(
            @Parameter(description = "ID de la dirección a actualizar", example = "5") @PathVariable Long id,
            @Valid @RequestBody DireccionRequestDTO dto) {
        return ResponseEntity.ok(direccionService.actualizarDireccion(id, dto));
    }



    // ELIMINACIÓN DE DIRECCIÓN
    @Operation(summary = "Eliminar dirección", description = "Elimina permanentemente una dirección de la base de datos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Dirección eliminada exitosamente (Sin contenido)"),
            @ApiResponse(responseCode = "404", description = "La dirección a eliminar no existe")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID de la dirección a eliminar", example = "5") @PathVariable Long id) {
        direccionService.eliminarDireccion(id);
        return ResponseEntity.noContent().build();
    }
}