package com.gamehub.userservice.services;

import com.gamehub.userservice.models.Direccion;
import com.gamehub.userservice.models.Dtos.DireccionRequestDTO;
import com.gamehub.userservice.models.Usuario;
import com.gamehub.userservice.repositories.DireccionRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DireccionServiceTest {

    //MOCKS
    @Mock
    private DireccionRepository direccionRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    // SUJETO A PROBAR
    @InjectMocks
    private DireccionService direccionService;

    // Variables globales para simular
    private DireccionRequestDTO direccionDto;
    private Direccion direccion;
    private Usuario usuarioSimulado;



    @BeforeEach
    void setUp() {

        // dueño de la dirección
        usuarioSimulado = new Usuario();
        usuarioSimulado.setId(1L);
        usuarioSimulado.setNombre("Ariel Rojo");

        //DTO de entrada
        direccionDto = new DireccionRequestDTO();
        direccionDto.setUsuarioId(1L);
        direccionDto.setComuna("Providencia");
        direccionDto.setCiudad("Santiago");
        direccionDto.setCalle("Av. Nueva Providencia");
        direccionDto.setNumero("1234");

        // entidad que simulara estar en la BD
        direccion = new Direccion();
        direccion.setId(10L);
        direccion.setComuna("Providencia");
        direccion.setCiudad("Santiago");
        direccion.setCalle("Av. Nueva Providencia");
        direccion.setNumero("1234");
        direccion.setUsuario(usuarioSimulado);
    }


    //      PRUEBAS PARA CREACIÓN DE DIRECCIÓN


    @Test
    void crearDireccion_exitosamente() {

        // GIVEN (usuario SI existe en la BD)
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioSimulado));
        when(direccionRepository.save(any(Direccion.class))).thenReturn(direccion);

        // WHEN (Cuando intentamos crear la dirección)
        Direccion resultado = direccionService.crearDireccion(direccionDto);

        // THEN (se crea y se asocia al usuario)
        assertNotNull(resultado);
        assertEquals("Providencia", resultado.getComuna());
        assertEquals("Ariel Rojo", resultado.getUsuario().getNombre());
        verify(direccionRepository, times(1)).save(any(Direccion.class));
    }


    @Test
    void crearDireccion_fallaPorUsuarioNoEncontrado() {

        // GIVEN (usuario NO existe en la BD)
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        // WHEN Y THEN (Debe lanzar excepcion y no intentar guardar la direccion)
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            direccionService.crearDireccion(direccionDto);
        });

        assertEquals("Usuario no encontrado", exception.getMessage());
        verify(direccionRepository, never()).save(any(Direccion.class));
    }


    //      PRUEBAS PARA BÚSQUEDA

    @Test
    void listarPorUsuario_exitoso() {

        // GIVEN
        List<Direccion> listaSimulada = Arrays.asList(direccion);
        when(direccionRepository.findByUsuarioId(1L)).thenReturn(listaSimulada);

        // WHEN
        List<Direccion> resultado = direccionService.listarPorUsuario(1L);

        // THEN
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        assertEquals("Santiago", resultado.get(0).getCiudad());
    }


    //      PRUEBAS PARA ACTUALIZACIÓN

    @Test
    void actualizarDireccion_exitoso() {

        // GIVEN
        direccionDto.setCalle("Calle Actualizada");
        direccionDto.setNumero("9999");

        when(direccionRepository.findById(10L)).thenReturn(Optional.of(direccion));
        when(direccionRepository.save(any(Direccion.class))).thenReturn(direccion);

        // WHEN
        Direccion resultado = direccionService.actualizarDireccion(10L, direccionDto);

        // THEN
        assertNotNull(resultado);
        assertEquals("Calle Actualizada", resultado.getCalle());
        assertEquals("9999", resultado.getNumero());
    }

    @Test
    void actualizarDireccion_fallaPorDireccionNoEncontrada() {

        // GIVEN
        when(direccionRepository.findById(99L)).thenReturn(Optional.empty());

        // WHEN Y THEN
        assertThrows(IllegalArgumentException.class, () -> {
            direccionService.actualizarDireccion(99L, direccionDto);
        });
    }

    //       PRUEBAS PARA ELIMINACIÓN

    @Test
    void eliminarDireccion_exitoso() {

        // GIVEN
        when(direccionRepository.existsById(10L)).thenReturn(true);
        doNothing().when(direccionRepository).deleteById(10L);

        // WHEN
        direccionService.eliminarDireccion(10L);

        // THEN
        verify(direccionRepository, times(1)).deleteById(10L);
    }

    @Test
    void eliminarDireccion_fallaPorDireccionNoEncontrada() {

        // GIVEN
        when(direccionRepository.existsById(99L)).thenReturn(false);

        // WHEN & THEN
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            direccionService.eliminarDireccion(99L);
        });

        assertEquals("La dirección no existe", exception.getMessage());
        verify(direccionRepository, never()).deleteById(anyLong());
    }
}