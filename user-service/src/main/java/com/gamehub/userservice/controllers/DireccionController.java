package com.gamehub.userservice.controllers;

import com.gamehub.userservice.models.Direccion;
import com.gamehub.userservice.models.Dtos.DireccionRequestDTO;
import com.gamehub.userservice.services.DireccionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/directions")
@RequiredArgsConstructor
public class DireccionController {

    private final DireccionService direccionService;


    // CREACIÓN DE DIRECCIÓN

    @PostMapping
    public ResponseEntity<Direccion> crear(@Valid @RequestBody DireccionRequestDTO dto) {
        return new ResponseEntity<>(direccionService.crearDireccion(dto), HttpStatus.CREATED);
    }


    // OBTENER DIRECCIONES POR USUARIO

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Direccion>> listarPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(direccionService.listarPorUsuario(usuarioId));
    }


    // ACTUALIZACIÓN DE DIRECCIÓN

    @PutMapping("/{id}")
    public ResponseEntity<Direccion> actualizar(@PathVariable Long id, @Valid @RequestBody DireccionRequestDTO dto) {
        return ResponseEntity.ok(direccionService.actualizarDireccion(id, dto));
    }


    // ELIMINACIÓN DE DIRECCIÓN

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        direccionService.eliminarDireccion(id);
        return ResponseEntity.noContent().build();
    }
}