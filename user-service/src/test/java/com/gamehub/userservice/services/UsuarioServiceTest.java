package com.gamehub.userservice.services;

import com.gamehub.userservice.models.Dtos.UsuarioRequestDTO;
import com.gamehub.userservice.models.Usuario;
import com.gamehub.userservice.repositories.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    //MOCK
    @Mock
    private UsuarioRepository usuarioRepository;

    //SUJETO A PROBAR
    @InjectMocks
    private UsuarioService usuarioService;

    // Variables para simular datos
    private UsuarioRequestDTO usuarioDto;
    private Usuario usuario;


    @BeforeEach
    void setUp() {
        // DTO DE ENTRADA
        usuarioDto = new UsuarioRequestDTO();
        usuarioDto.setNombre("Ariel Rojo");
        usuarioDto.setEmail("ariel.rojo@gamehub.com");
        usuarioDto.setTelefono("987654321");
        usuarioDto.setRol("CLIENTE");

        //entidad de la Base de Datos
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Ariel Rojo");
        usuario.setEmail("ariel.rojo@gamehub.com");
        usuario.setTelefono("987654321");
        usuario.setRol("CLIENTE");
        usuario.setEstado(true); // Activo por defecto
    }

    //PRUEBAS PARA CREACIÓN DE USUARIO

    @Test
    void crearUsuario_exitosamente() {

        // GIVEN (no existe ningún usuario con ese email en la BD)
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // WHEN
        Usuario resultado = usuarioService.crearUsuario(usuarioDto);

        // THEN (se crea exitosamente y está activo)
        assertNotNull(resultado);
        assertEquals("Ariel Rojo", resultado.getNombre());
        assertTrue(resultado.getEstado(), "El usuario debe crearse con estado ACTIVO por defecto");
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void crearUsuario_fallaPorEmailDuplicado() {

        // GIVEN (ya existe un usuario con ese email en la BD)
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.of(usuario));

        // WHEN & THEN (Cuando intentamos crearlo debe lanzar la excepción)
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.crearUsuario(usuarioDto);
        });

        assertEquals("El email ya se encuentra registrado.", exception.getMessage());
        verify(usuarioRepository, never()).save(any(Usuario.class)); // Verificamosque no intento guardar
    }

    //      PRUEBAS PARA BÚSQUEDA

    @Test
    void buscarPorId_exitoso() {
        // GIVEN
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        // WHEN
        Usuario resultado = usuarioService.buscarPorId(1L);

        // THEN
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
    }

    @Test
    void buscarPorId_fallaPorqueNoExiste() {

        // GIVEN
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        // WHEN Y THEN
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.buscarPorId(99L);
        });

        assertEquals("Usuario no encontrado con ID: 99", exception.getMessage());
    }

    @Test
    void listarPorRolOEstado_filtraPorRol() {

        // GIVEN
        List<Usuario> listaMock = Arrays.asList(usuario);
        when(usuarioRepository.findByRol("CLIENTE")).thenReturn(listaMock);

        // WHEN
        List<Usuario> resultado = usuarioService.listarPorRolOEstado("CLIENTE", null);

        // THEN
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        assertEquals("CLIENTE", resultado.get(0).getRol());
    }

    //       PRUEBAS PARA ACTUALIZACIÓN Y DESACTIVACIÓN

    @Test
    void actualizarDatos_exitoso() {

        // GIVEN
        usuarioDto.setNombre("Ariel Actualizado");
        usuarioDto.setTelefono("111222333");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // WHEN
        Usuario resultado = usuarioService.actualizarDatos(1L, usuarioDto);

        // THEN
        assertNotNull(resultado);
        assertEquals("Ariel Actualizado", resultado.getNombre());
        assertEquals("111222333", resultado.getTelefono());
    }

    @Test
    void desactivarUsuario_exitoso() {

        // GIVEN
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // WHEN
        Usuario resultado = usuarioService.desactivarUsuario(1L);

        // THEN
        assertFalse(resultado.getEstado(), "El estado del usuario debe cambiar a false (inactivo)");
        verify(usuarioRepository, times(1)).save(usuario);
    }
}