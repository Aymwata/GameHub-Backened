package com.gamehub.promotionservice.controllers;

import com.gamehub.promotionservice.models.Promotion;
import com.gamehub.promotionservice.models.PromotionDTO;
import com.gamehub.promotionservice.services.PromotionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/promotions") // Agregamos versionamiento de API
@RequiredArgsConstructor // Inyección por constructor (más seguro que @Autowired)
public class PromotionControllers {

    private final PromotionService service;

    @PostMapping
    public ResponseEntity<Promotion> crear(@Valid @RequestBody PromotionDTO dto) {
        return new ResponseEntity<>(service.crear(dto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Promotion>> listar(
            @RequestParam(required = false) Boolean vigentes) {
        return ResponseEntity.ok(service.listar(vigentes));
    }

    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<Promotion> buscarPorCodigo(@PathVariable String codigo) {
        return ResponseEntity.ok(service.buscarPorCodigo(codigo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Promotion> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody PromotionDTO dto) {
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desactivar(@PathVariable Long id) {
        service.desactivar(id);
    }
}