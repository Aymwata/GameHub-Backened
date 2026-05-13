package com.gamehub.userservice.Controllers;

import com.gamehub.userservice.Models.Direccion;
import com.gamehub.userservice.Models.Dtos.DireccionRequestDTO;
import com.gamehub.userservice.Services.DireccionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/direcciones")
@RequiredArgsConstructor
public class DireccionController {

    private final DireccionService direccionService;

    @PostMapping
    public ResponseEntity<Direccion> crear(@Valid @RequestBody DireccionRequestDTO dto) {
        return new ResponseEntity<>(direccionService.crearDireccion(dto), HttpStatus.CREATED);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Direccion>> listarPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(direccionService.listarPorUsuario(usuarioId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Direccion> actualizar(@PathVariable Long id, @Valid @RequestBody DireccionRequestDTO dto) {
        return ResponseEntity.ok(direccionService.actualizarDireccion(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        direccionService.eliminarDireccion(id);
        return ResponseEntity.noContent().build();
    }
}