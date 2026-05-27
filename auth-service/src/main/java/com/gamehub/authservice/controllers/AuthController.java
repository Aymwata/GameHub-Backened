package com.gamehub.authservice.controllers;


import com.gamehub.authservice.models.CuentaAcceso;
import com.gamehub.authservice.models.Dtos.CuentaAccesoRequestDTO;
import com.gamehub.authservice.models.Dtos.LoginRequestDTO;
import com.gamehub.authservice.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // CREACION DE CUENTA

    @PostMapping("/cuentas")
    public ResponseEntity<CuentaAcceso> crearCuenta(@Valid @RequestBody CuentaAccesoRequestDTO dto) {
        return new ResponseEntity<>(authService.crearCuenta(dto), HttpStatus.CREATED);
    }

    // LISTADO DE CUENTAS

    @GetMapping("/cuentas")
    public ResponseEntity<List<CuentaAcceso>> listarCuentas() {
        return ResponseEntity.ok(authService.listarCuentas());
    }

    // BUSQUEDA DE CUENTA POR ID

    @GetMapping("/cuentas/{id}")
    public ResponseEntity<CuentaAcceso> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(authService.buscarPorId(id));
    }

    // BUSQUEDA DE CUENTA POR EMAIL

    @GetMapping("/cuentas/buscar")
    public ResponseEntity<CuentaAcceso> buscarPorEmail(@RequestParam String email) {
        return ResponseEntity.ok(authService.buscarPorEmail(email));
    }

    // ACTUALIZACION DE CONTRASENA O ROL

    @PutMapping("/cuentas/{id}")
    public ResponseEntity<CuentaAcceso> actualizarCuenta(
            @PathVariable Long id,
            @Valid @RequestBody CuentaAccesoRequestDTO dto) {
        return ResponseEntity.ok(authService.actualizarCuenta(id, dto));
    }

    // DESACTIVACION DE CUENTA

    @PatchMapping("/cuentas/{id}/desactivar")
    public ResponseEntity<CuentaAcceso> desactivarCuenta(@PathVariable Long id) {
        return ResponseEntity.ok(authService.desactivarCuenta(id));
    }

    // SIMULACION DE INICIO DE SESION

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody LoginRequestDTO dto) {
        String mensaje = authService.simularLogin(dto);
        return ResponseEntity.ok(Map.of("mensaje", mensaje));
    }
}