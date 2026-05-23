package com.gamehub.userservice.services;

import com.gamehub.userservice.models.Dtos.UsuarioRequestDTO;
import com.gamehub.userservice.models.Usuario;
import com.gamehub.userservice.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    // CREACIÓN DE PERFIL DE USUARIO
    // Recibe el DTO validado, verifica la regla de negocio "No duplicar email" y guarda el nuevo usuario con estado activo por defecto.
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

    // BÚSQUEDA DE USUARIOS
    // Filtra la lista de usuarios dependiendo de si se da un rol específico, un estado, o retorna todos si no hay filtros.
    public List<Usuario> listarPorRolOEstado(String rol, Boolean estado) {
        if (rol != null) {
            return usuarioRepository.findByRol(rol);
        } else if (estado != null) {
            return usuarioRepository.findByEstado(estado);
        }
        return usuarioRepository.findAll();
    }

    // BÚSQUEDA INDIVIDUAL POR ID
    // Busca a un usuario por su identificador único, si no existe, lanza una excepción para detenerlo y evitar errores nulos.
    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + id));
    }

    // ACTUALIZACIÓN DE DATOS DE CONTACTO
    // Permite modificar el nombre y teléfono de un usuario existente, validando primero que el usuario exista en la base de datos.
    public Usuario actualizarDatos(Long id, UsuarioRequestDTO dto) {
        log.info("Actualizando datos del usuario ID: {}", id);
        Usuario usuario = buscarPorId(id);

        usuario.setNombre(dto.getNombre());
        usuario.setTelefono(dto.getTelefono());

        return usuarioRepository.save(usuario);
    }

    // DESACTIVACIÓN LÓGICA DE USUARIO
    // Cambia el flag de estado a inactivo (asi no se borra el registro)
    public Usuario desactivarUsuario(Long id) {
        log.info("Desactivando usuario ID: {}", id);
        Usuario usuario = buscarPorId(id);
        usuario.setEstado(false);
        return usuarioRepository.save(usuario);
    }

    // BÚSQUEDA POR CORREO ELECTRÓNICO
    // Busca un usuario por su email.
    public Usuario buscarPorEmail(String email) {
        log.info("Buscando usuario por email: {}", email);
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con el email: " + email));
    }
}