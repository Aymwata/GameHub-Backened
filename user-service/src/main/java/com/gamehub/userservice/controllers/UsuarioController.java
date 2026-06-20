package com.gamehub.userservice.controllers;

import com.gamehub.userservice.models.Usuario;
import com.gamehub.userservice.models.Dtos.UsuarioRequestDTO;
import com.gamehub.userservice.services.UsuarioService;
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
@RequestMapping("/api/users")
@RequiredArgsConstructor

@Tag(name = "Gestion de Usuarios", description = "Endpoints para la creación, búsqueda y administración de perfiles de usuario en GameHub.")
public class UsuarioController {

    private final UsuarioService usuarioService;



    // CREACIÓN DE USUARIO
    @Operation(summary = "Crear un nuevo usuario", description = "Registra un usuario en la plataforma con sus datos básicos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Errores de validación en los datos enviados")
    })
    @PostMapping
    public ResponseEntity<Usuario> crearUsuario(@Valid @RequestBody UsuarioRequestDTO dto) {
        Usuario nuevoUsuario = usuarioService.crearUsuario(dto);
        return new ResponseEntity<>(nuevoUsuario, HttpStatus.CREATED);
    }



    // LISTADO DE USUARIOS
    @Operation(summary = "Listar usuarios", description = "Obtiene una lista de usuarios. Permite filtrar opcionalmente por rol o por estado (activo/inactivo).")
    @ApiResponse(responseCode = "200", description = "Lista de usuarios recuperada exitosamente")
    @GetMapping
    public ResponseEntity<List<Usuario>> listarUsuarios(
            @Parameter(description = "Filtro opcional por rol del usuario", example = "CLIENTE") @RequestParam(required = false) String rol,
            @Parameter(description = "Filtro opcional por estado (true=activo, false=inactivo)", example = "true") @RequestParam(required = false) Boolean estado) {
        return ResponseEntity.ok(usuarioService.listarPorRolOEstado(rol, estado));
    }



    // BÚSQUEDA DE USUARIO POR ID (GET)
    @Operation(summary = "Buscar usuario por ID", description = "Recupera la información detallada de un usuario específico mediante su identificador único.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(
            @Parameter(description = "ID único del usuario", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }



    // ACTUALIZACIÓN DE DATOS
    @Operation(summary = "Actualizar datos del usuario", description = "Modifica la información de un usuario existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizarUsuario(
            @Parameter(description = "ID del usuario a actualizar", example = "1") @PathVariable Long id,
            @Valid @RequestBody UsuarioRequestDTO dto) {
        return ResponseEntity.ok(usuarioService.actualizarDatos(id, dto));
    }



    // DESACTIVACIÓN DE USUARIO
    @Operation(summary = "Desactivar usuario", description = "Realiza un borrado lógico (soft delete) cambiando el estado del usuario a inactivo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario desactivado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Usuario> desactivarUsuario(
            @Parameter(description = "ID del usuario a desactivar", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.desactivarUsuario(id));
    }



    // BÚSQUEDA DE USUARIO POR EMAIL (GET)
    @Operation(summary = "Buscar usuario por Email", description = "Permite localizar a un usuario exacto utilizando su correo electrónico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "No se encontró ningún usuario con ese correo")
    })
    @GetMapping("/buscar")
    public ResponseEntity<Usuario> buscarPorEmail(
            @Parameter(description = "Correo electrónico del usuario", example = "jugador@gamehub.com") @RequestParam String email) {
        return ResponseEntity.ok(usuarioService.buscarPorEmail(email));
    }
}