package com.gamehub.userservice.Services;

import com.gamehub.userservice.Models.Dtos.UsuarioRequestDTO;
import com.gamehub.userservice.Models.Usuario;
import com.gamehub.userservice.Repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public Usuario crearUsuario(UsuarioRequestDTO dto) {
        log.info("Iniciando creación de usuario con email: {}", dto.getEmail());

        // Regla de negocio: No duplicar email
        if (usuarioRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("El email ya se encuentra registrado.");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setEmail(dto.getEmail());
        usuario.setTelefono(dto.getTelefono());
        usuario.setRol(dto.getRol());
        usuario.setEstado(true); // Activo por defecto

        Usuario guardado = usuarioRepository.save(usuario);
        log.info("Usuario creado exitosamente con ID: {}", guardado.getId());
        return guardado;
    }

    public List<Usuario> listarPorRolOEstado(String rol, Boolean estado) {
        if (rol != null) {
            return usuarioRepository.findByRol(rol);
        } else if (estado != null) {
            return usuarioRepository.findByEstado(estado);
        }
        return usuarioRepository.findAll();
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + id));
    }

    public Usuario actualizarDatos(Long id, UsuarioRequestDTO dto) {
        log.info("Actualizando datos del usuario ID: {}", id);
        Usuario usuario = buscarPorId(id);

        usuario.setNombre(dto.getNombre());
        usuario.setTelefono(dto.getTelefono());
        // Se asume que el email no se cambia o se maneja en otro endpoint específico

        return usuarioRepository.save(usuario);
    }

    public Usuario desactivarUsuario(Long id) {
        log.info("Desactivando usuario ID: {}", id);
        Usuario usuario = buscarPorId(id);
        usuario.setEstado(false);
        return usuarioRepository.save(usuario);
    }
}
