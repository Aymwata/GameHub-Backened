package com.gamehub.authservice.controllers;

import com.gamehub.authservice.models.CuentaAcceso;
import com.gamehub.authservice.models.Dtos.CuentaAccesoRequestDTO;
import com.gamehub.authservice.models.Dtos.LoginRequestDTO;
import com.gamehub.authservice.models.Dtos.RegistroRequestDTO;
import com.gamehub.authservice.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación y cuentas", description = "Registro, sesiones JWT y administración de cuentas.")
public class AuthController {
    private static final String REFRESH_COOKIE = "gamehub_refresh";

    private final AuthService authService;
    private final boolean secureCookie;

    public AuthController(AuthService authService, @Value("${gamehub.auth.cookie-secure:true}") boolean secureCookie) {
        this.authService = authService;
        this.secureCookie = secureCookie;
    }

    // CREACION DE CUENTA
    @Operation(summary = "Crear una nueva cuenta", description = "Registra una nueva cuenta de acceso en el sistema con sus credenciales y rol.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cuenta creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos (ej. formato de email incorrecto)")
    })
    @PostMapping("/cuentas")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<CuentaAcceso> crearCuenta(@Valid @RequestBody CuentaAccesoRequestDTO dto) {
        return new ResponseEntity<>(authService.crearCuenta(dto), HttpStatus.CREATED);
    }



    // LISTADO DE CUENTAS
    @Operation(summary = "Listar todas las cuentas", description = "Obtiene una lista completa de todas las cuentas registradas en la base de datos.")
    @ApiResponse(responseCode = "200", description = "Lista recuperada exitosamente")
    @GetMapping("/cuentas")
    @SecurityRequirement(name = "bearerAuth")
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
    @SecurityRequirement(name = "bearerAuth")
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
    @SecurityRequirement(name = "bearerAuth")
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
    @SecurityRequirement(name = "bearerAuth")
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
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<CuentaAcceso> desactivarCuenta(
            @Parameter(description = "ID de la cuenta a desactivar", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(authService.desactivarCuenta(id));
    }



    @Operation(summary = "Iniciar sesión como cliente o administrador",
            description = "Verifica las credenciales y el perfil solicitado. Devuelve un JWT y una cookie de renovación.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login exitoso"),
            @ApiResponse(responseCode = "401", description = "Credenciales incorrectas o cuenta inactiva"),
            @ApiResponse(responseCode = "403", description = "La cuenta no pertenece al perfil solicitado")
    })
    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthService.Acceso> login(@Valid @RequestBody LoginRequestDTO dto) {
        return acceso(authService.login(dto));
    }

    @Operation(summary = "Registrar una cuenta de cliente")
    @PostMapping(value = "/registro", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthService.UsuarioSesion> registrar(@Valid @RequestBody RegistroRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registrar(dto));
    }

    @Operation(summary = "Renovar la sesión con la cookie HttpOnly")
    @PostMapping("/refresh")
    public ResponseEntity<AuthService.Acceso> renovar(
            @RequestHeader("X-GameHub-Session") String solicitud,
            @Parameter(hidden = true) @CookieValue(value = REFRESH_COOKIE, required = false) String refresh) {
        verificarSolicitudSesion(solicitud);
        return acceso(authService.renovar(refresh));
    }

    @Operation(summary = "Cerrar la sesión y revocar su renovación")
    @PostMapping("/logout")
    public ResponseEntity<Void> cerrarSesion(
            @RequestHeader("X-GameHub-Session") String solicitud,
            @Parameter(hidden = true) @CookieValue(value = REFRESH_COOKIE, required = false) String refresh) {
        verificarSolicitudSesion(solicitud);
        authService.cerrarSesion(refresh);
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, cookie("", Duration.ZERO).toString())
                .build();
    }

    private ResponseEntity<AuthService.Acceso> acceso(AuthService.Sesion sesion) {
        Duration restante = Duration.between(Instant.now(), sesion.venceRefresh());
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .header(HttpHeaders.SET_COOKIE, cookie(sesion.refreshToken(), restante.isNegative() ? Duration.ZERO : restante).toString())
                .body(sesion.acceso());
    }

    private ResponseCookie cookie(String valor, Duration duracion) {
        return ResponseCookie.from(REFRESH_COOKIE, valor).httpOnly(true).secure(secureCookie)
                .sameSite("Strict").path("/api/auth").maxAge(duracion).build();
    }

    private static void verificarSolicitudSesion(String solicitud) {
        // Browsers cannot send this header from another origin without CORS permission.
        if (!"1".equals(solicitud)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solicitud de sesión inválida.");
        }
    }
}
