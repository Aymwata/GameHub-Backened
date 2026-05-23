package com.gamehub.userservice.controllers;
import com.gamehub.userservice.models.Usuario;
import com.gamehub.userservice.models.Dtos.UsuarioRequestDTO;
import com.gamehub.userservice.services.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    // CREACIÓN DE USUARIO

    @PostMapping
    public ResponseEntity<Usuario> crearUsuario(@Valid @RequestBody UsuarioRequestDTO dto) {
        Usuario nuevoUsuario = usuarioService.crearUsuario(dto);
        return new ResponseEntity<>(nuevoUsuario, HttpStatus.CREATED);
    }


    // LISTADO DE USUARIOS

    @GetMapping
    public ResponseEntity<List<Usuario>> listarUsuarios(
            @RequestParam(required = false) String rol,
            @RequestParam(required = false) Boolean estado) {
        return ResponseEntity.ok(usuarioService.listarPorRolOEstado(rol, estado));
    }


    // BÚSQUEDA DE USUARIO POR ID (GET)

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }


    // ACTUALIZACIÓN DE DATOS

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizarUsuario(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioRequestDTO dto) {
        return ResponseEntity.ok(usuarioService.actualizarDatos(id, dto));
    }


    // DESACTIVACIÓN DE USUARIO

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Usuario> desactivarUsuario(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.desactivarUsuario(id));
    }


    // BÚSQUEDA DE USUARIO POR EMAIL (GET)

    @GetMapping("/buscar")
    public ResponseEntity<Usuario> buscarPorEmail(@RequestParam String email) {
        return ResponseEntity.ok(usuarioService.buscarPorEmail(email));
    }
}