package com.gamehub.paymentservice.controllers;

import com.gamehub.paymentservice.models.dto.*;
import com.gamehub.paymentservice.services.PagoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PagoService pagoService;

    @PostMapping
    public ResponseEntity<PagoResponseDTO> crearPago(@Valid @RequestBody PagoRequestDTO dto) {
        return new ResponseEntity<>(pagoService.procesarPago(dto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<PagoResponseDTO>> obtenerPagos(
            @RequestParam(required = false) Long ordenId,
            @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) String estado) {
        return ResponseEntity.ok(pagoService.listarPagos(ordenId, clienteId, estado));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PagoResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pagoService.buscarPorId(id));
    }

    @PutMapping("/{id}/anular")
    public ResponseEntity<PagoResponseDTO> anularPago(@PathVariable Long id) {
        return ResponseEntity.ok(pagoService.anularPago(id));
    }
}