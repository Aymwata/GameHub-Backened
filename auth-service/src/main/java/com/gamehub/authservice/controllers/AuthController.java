package com.gamehub.authservice.controllers;

import com.gamehub.authservice.models.CuentaAcceso;
import com.gamehub.authservice.models.Dtos.CuentaAccesoRequestDTO;
import com.gamehub.authservice.models.Dtos.LoginRequestDTO;
import com.gamehub.authservice.services.AuthService;
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
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticacion y Cuentas", description = "Operaciones CRUD para la gestión de cuentas de acceso y simulación de login para GameHub.")
public class AuthController {

    private final AuthService authService;

    // CREACION DE CUENTA
    @Operation(summary = "Crear una nueva cuenta", description = "Registra una nueva cuenta de acceso en el sistema con sus credenciales y rol.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cuenta creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos (ej. formato de email incorrecto)")
    })
    @PostMapping("/cuentas")
    public ResponseEntity<CuentaAcceso> crearCuenta(@Valid @RequestBody CuentaAccesoRequestDTO dto) {
        return new ResponseEntity<>(authService.crearCuenta(dto), HttpStatus.CREATED);
    }



    // LISTADO DE CUENTAS
    @Operation(summary = "Listar todas las cuentas", description = "Obtiene una lista completa de todas las cuentas registradas en la base de datos.")
    @ApiResponse(responseCode = "200", description = "Lista recuperada exitosamente")
    @GetMapping("/cuentas")
    public ResponseEntity<List<CuentaAcceso>> listarCuentas() {
        return ResponseEntity.ok(authService.listarCuentas());
    }



    // BUSQUEDA DE CUENTA POR ID
    @Operation(summary = "Buscar cuenta por ID", description = "Obtiene los detalles de una cuenta específica utilizando su identificador único.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cuenta encontrada"),
            @ApiResponse(responseCode = "404", description = "La cuenta con el ID especificado no existe")
    })
    @GetMapping("/cuentas/{id}")
    public ResponseEntity<CuentaAcceso> buscarPorId(
            @Parameter(description = "ID único de la cuenta", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(authService.buscarPorId(id));
    }



    // BUSQUEDA DE CUENTA POR EMAIL
    @Operation(summary = "Buscar cuenta por Email", description = "Busca una cuenta exacta coincidiendo con la dirección de correo electrónico proporcionada.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cuenta encontrada"),
            @ApiResponse(responseCode = "404", description = "No existe ninguna cuenta asociada a ese email")
    })
    @GetMapping("/cuentas/buscar")
    public ResponseEntity<CuentaAcceso> buscarPorEmail(
            @Parameter(description = "Correo electrónico a buscar", example = "usuario@gamehub.com") @RequestParam String email) {
        return ResponseEntity.ok(authService.buscarPorEmail(email));
    }



    // ACTUALIZACION DE CONTRASENA O ROL
    @Operation(summary = "Actualizar cuenta", description = "Actualiza los datos de una cuenta existente, como la contraseña o el rol asignado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cuenta actualizada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "La cuenta a actualizar no existe")
    })
    @PutMapping("/cuentas/{id}")
    public ResponseEntity<CuentaAcceso> actualizarCuenta(
            @Parameter(description = "ID de la cuenta a modificar", example = "1") @PathVariable Long id,
            @Valid @RequestBody CuentaAccesoRequestDTO dto) {
        return ResponseEntity.ok(authService.actualizarCuenta(id, dto));
    }



    // DESACTIVACION DE CUENTA
    @Operation(summary = "Desactivar cuenta", description = "Cambia el estado de una cuenta a inactiva sin borrarla de la base de datos (Soft Delete).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cuenta desactivada exitosamente"),
            @ApiResponse(responseCode = "404", description = "La cuenta a desactivar no existe")
    })
    @PatchMapping("/cuentas/{id}/desactivar")
    public ResponseEntity<CuentaAcceso> desactivarCuenta(
            @Parameter(description = "ID de la cuenta a desactivar", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(authService.desactivarCuenta(id));
    }



    // SIMULACION DE INICIO DE SESION
    @Operation(summary = "Simulación de Login", description = "Verifica las credenciales enviadas y simula el proceso de inicio de sesión devolviendo un mensaje de éxito.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login exitoso"),
            @ApiResponse(responseCode = "401", description = "Credenciales incorrectas (Email o contraseña inválidos)")
    })
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody LoginRequestDTO dto) {
        String mensaje = authService.simularLogin(dto);
        return ResponseEntity.ok(Map.of("mensaje", mensaje));
    }
}