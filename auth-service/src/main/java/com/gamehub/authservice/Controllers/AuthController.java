package com.gamehub.authservice.Controllers;


import com.gamehub.authservice.Models.CuentaAcceso;
import com.gamehub.authservice.Models.Dtos.CuentaAccesoRequestDTO;
import com.gamehub.authservice.Models.Dtos.LoginRequestDTO;
import com.gamehub.authservice.Services.AuthService;
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

    // Crear cuenta
    @PostMapping("/cuentas")
    public ResponseEntity<CuentaAcceso> crearCuenta(@Valid @RequestBody CuentaAccesoRequestDTO dto) {
        return new ResponseEntity<>(authService.crearCuenta(dto), HttpStatus.CREATED);
    }

    // Listar cuentas
    @GetMapping("/cuentas")
    public ResponseEntity<List<CuentaAcceso>> listarCuentas() {
        return ResponseEntity.ok(authService.listarCuentas());
    }

    // Buscar por ID
    @GetMapping("/cuentas/{id}")
    public ResponseEntity<CuentaAcceso> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(authService.buscarPorId(id));
    }

    // Buscar por Email (usando RequestParam para diferenciar de la ruta por ID)
    @GetMapping("/cuentas/buscar")
    public ResponseEntity<CuentaAcceso> buscarPorEmail(@RequestParam String email) {
        return ResponseEntity.ok(authService.buscarPorEmail(email));
    }

    // Actualizar contraseña o rol
    @PutMapping("/cuentas/{id}")
    public ResponseEntity<CuentaAcceso> actualizarCuenta(
            @PathVariable Long id,
            @Valid @RequestBody CuentaAccesoRequestDTO dto) {
        return ResponseEntity.ok(authService.actualizarCuenta(id, dto));
    }

    // Desactivar cuenta
    @PatchMapping("/cuentas/{id}/desactivar")
    public ResponseEntity<CuentaAcceso> desactivarCuenta(@PathVariable Long id) {
        return ResponseEntity.ok(authService.desactivarCuenta(id));
    }

    // Simulación de Login
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody LoginRequestDTO dto) {
        String mensaje = authService.simularLogin(dto);
        return ResponseEntity.ok(Map.of("mensaje", mensaje));
    }
}
