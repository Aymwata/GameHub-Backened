package com.gamehub.reviewservice.Controllers;

import com.gamehub.reviewservice.Models.DTOs.ResenaRequestDTO;
import com.gamehub.reviewservice.Models.Resena;
import com.gamehub.reviewservice.Service.ResenaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resenas")
@RequiredArgsConstructor
public class ResenaController {

    private final ResenaService resenaService;

    @PostMapping
    public ResponseEntity<Resena> crearResena(@Valid @RequestBody ResenaRequestDTO request) {
        return new ResponseEntity<>(resenaService.crearResena(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Resena>> listarResenas(
            @RequestParam(required = false) Long productoId,
            @RequestParam(required = false) Long usuarioId) {
        return ResponseEntity.ok(resenaService.listarResenas(productoId, usuarioId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resena> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(resenaService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Resena> actualizarResena(
            @PathVariable Long id,
            @Valid @RequestBody ResenaRequestDTO request) {
        return ResponseEntity.ok(resenaService.actualizarResena(id, request));
    }

    @PutMapping("/{id}/moderar")
    public ResponseEntity<Resena> moderarResena(
            @PathVariable Long id,
            @RequestParam String nuevoEstado) {
        return ResponseEntity.ok(resenaService.moderarResena(id, nuevoEstado));
    }
}