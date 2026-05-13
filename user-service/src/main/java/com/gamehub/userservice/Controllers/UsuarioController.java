package com.gamehub.userservice.Controllers;
import com.gamehub.userservice.Models.Usuario;
import com.gamehub.userservice.Models.Dtos.UsuarioRequestDTO;
import com.gamehub.userservice.Services.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;





@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    // 1. Crear usuario
    @PostMapping
    public ResponseEntity<Usuario> crearUsuario(@Valid @RequestBody UsuarioRequestDTO dto) {
        Usuario nuevoUsuario = usuarioService.crearUsuario(dto);
        return new ResponseEntity<>(nuevoUsuario, HttpStatus.CREATED);
    }

    // 2. Listar usuarios (por rol o estado)
    @GetMapping
    public ResponseEntity<List<Usuario>> listarUsuarios(
            @RequestParam(required = false) String rol,
            @RequestParam(required = false) Boolean estado) {
        return ResponseEntity.ok(usuarioService.listarPorRolOEstado(rol, estado));
    }

    // 3. Buscar usuario por ID
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }

    // 4. Actualizar datos de contacto
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizarUsuario(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioRequestDTO dto) {
        return ResponseEntity.ok(usuarioService.actualizarDatos(id, dto));
    }

    // 5. Desactivar usuario
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Usuario> desactivarUsuario(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.desactivarUsuario(id));
    }

    @GetMapping("/buscar")
    public ResponseEntity<Usuario> buscarPorEmail(@RequestParam String email) {
        // Usamos el método que ya existe en el service
        return ResponseEntity.ok(usuarioService.buscarPorEmail(email));
    }
}