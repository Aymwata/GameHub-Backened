package com.gamehub.reviewservice.services;
import com.gamehub.reviewservice.Client.OrderClient;
import com.gamehub.reviewservice.Client.ProductClient;
import com.gamehub.reviewservice.Client.UserClient;
import com.gamehub.reviewservice.Exceptions.BusinessRuleException;
import com.gamehub.reviewservice.Models.DTOs.*;
import com.gamehub.reviewservice.Models.Resena;
import com.gamehub.reviewservice.Repositories.ResenaRepository;
import com.gamehub.reviewservice.Service.ResenaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Collections;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ResenaServiceTest {

    // MOCKS
    @Mock private ResenaRepository resenaRepository;
    @Mock private UserClient userClient;
    @Mock private ProductClient productClient;
    @Mock private OrderClient orderClient;

    // SUJETO A PROBAR
    @InjectMocks
    private ResenaService resenaService;

    // Variables globales
    private ResenaRequestDTO requestDTO;
    private Resena resena;
    private UsuarioDTO usuarioDTO;
    private ProductoDTO productoDTO;
    private OrdenDTO ordenDTO;



    @BeforeEach
    void setUp() {

        // DTO de entrada, lo que envia el usuario
        requestDTO = new ResenaRequestDTO();
        requestDTO.setUsuarioId(1L);
        requestDTO.setProductoId(10L);
        requestDTO.setOrdenId(100L);
        requestDTO.setPuntuacion(5);
        requestDTO.setComentario("¡Excelente juego, superó mis expectativas!");

        // entidad de BD simulada
        resena = new Resena();
        resena.setId(1L);
        resena.setUsuarioId(1L);
        resena.setProductoId(10L);
        resena.setOrdenId(100L);
        resena.setPuntuacion(5);
        resena.setComentario("¡Excelente juego, superó mis expectativas!");
        resena.setEstado("ACTIVA");

        // respuestas simuladas de los otros microservicios
        usuarioDTO = new UsuarioDTO();
        usuarioDTO.setId(1L);
        usuarioDTO.setEstado(true);

        productoDTO = new ProductoDTO();
        productoDTO.setId(10L);
        productoDTO.setEstado(true);

        ordenDTO = new OrdenDTO();
        ordenDTO.setId(100L);
        ordenDTO.setUsuarioId(1L); // <-------  la orden pertenece al usuario 1
        DetalleOrdenDTO detalle = new DetalleOrdenDTO();
        detalle.setProductoId(10L); // <-------     la orden contiene el producto 10
        ordenDTO.setDetalles(Collections.singletonList(detalle));
    }


    //  PRUEBAS PARA CREACIÓN DE RESEÑAS

    @Test
    void crearResena_exitosamente() {

        // GIVEN (todo ok)
        when(resenaRepository.existsByOrdenIdAndProductoId(100L, 10L)).thenReturn(false);
        when(userClient.buscarPorId(1L)).thenReturn(usuarioDTO);
        when(productClient.obtenerProducto(10L)).thenReturn(productoDTO);
        when(orderClient.buscarPorId(100L)).thenReturn(ordenDTO);
        when(resenaRepository.save(any(Resena.class))).thenReturn(resena);

        // WHEN
        Resena resultado = resenaService.crearResena(requestDTO);

        // THEN
        assertNotNull(resultado);
        assertEquals("ACTIVA", resultado.getEstado());
        verify(resenaRepository, times(1)).save(any(Resena.class));
    }

    @Test
    void crearResena_fallaPorResenaDuplicada() {

        // GIVEN reseña duplicada
        when(resenaRepository.existsByOrdenIdAndProductoId(100L, 10L)).thenReturn(true);

        // WHEN Y THEN
        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            resenaService.crearResena(requestDTO);
        });

        assertEquals("Ya existe una reseña para este producto en esta orden.", exception.getMessage());
        verify(userClient, never()).buscarPorId(anyLong()); // Corta de inmediato
    }

    @Test
    void crearResena_fallaPorUsuarioInactivo() {
        // GIVEN
        when(resenaRepository.existsByOrdenIdAndProductoId(100L, 10L)).thenReturn(false);
        usuarioDTO.setEstado(false);
        when(userClient.buscarPorId(1L)).thenReturn(usuarioDTO);

        // WHEN Y THEN
        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            resenaService.crearResena(requestDTO);
        });

        assertEquals("El usuario está inactivo.", exception.getMessage());
    }

    @Test
    void crearResena_fallaPorqueLaOrdenEsDeOtroUsuario() {
        // GIVEN
        when(resenaRepository.existsByOrdenIdAndProductoId(100L, 10L)).thenReturn(false);
        when(userClient.buscarPorId(1L)).thenReturn(usuarioDTO);
        when(productClient.obtenerProducto(10L)).thenReturn(productoDTO);

        ordenDTO.setUsuarioId(99L);
        when(orderClient.buscarPorId(100L)).thenReturn(ordenDTO);

        // WHEN Y THEN
        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            resenaService.crearResena(requestDTO);
        });

        assertEquals("La orden no pertenece al usuario especificado.", exception.getMessage());
    }

    @Test
    void crearResena_fallaPorqueNoComproEseProducto() {
        // GIVEN
        when(resenaRepository.existsByOrdenIdAndProductoId(100L, 10L)).thenReturn(false);
        when(userClient.buscarPorId(1L)).thenReturn(usuarioDTO);
        when(productClient.obtenerProducto(10L)).thenReturn(productoDTO);

        ordenDTO.setDetalles(Collections.emptyList());
        when(orderClient.buscarPorId(100L)).thenReturn(ordenDTO);

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            resenaService.crearResena(requestDTO);
        });
        assertEquals("El usuario no compró este producto en la orden especificada. Solo puede reseñar quien compró el producto.", exception.getMessage());
    }


    //  PRUEBAS PARA OTRAS OPERACIONES

    @Test
    void buscarPorId_exitoso() {

        // GIVEN
        when(resenaRepository.findById(1L)).thenReturn(Optional.of(resena));

        // WHEN
        Resena resultado = resenaService.buscarPorId(1L);

        // THEN
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
    }

    @Test
    void actualizarResena_exitoso() {
        // GIVEN
        requestDTO.setComentario("Cambié de opinión, no es tan bueno.");
        requestDTO.setPuntuacion(3);

        when(resenaRepository.findById(1L)).thenReturn(Optional.of(resena));
        when(resenaRepository.save(any(Resena.class))).thenReturn(resena);

        // WHEN
        Resena resultado = resenaService.actualizarResena(1L, requestDTO);

        // THEN
        assertEquals("Cambié de opinión, no es tan bueno.", resultado.getComentario());
        assertEquals(3, resultado.getPuntuacion());
    }

    @Test
    void moderarResena_exitoso() {

        // GIVEN
        when(resenaRepository.findById(1L)).thenReturn(Optional.of(resena));
        when(resenaRepository.save(any(Resena.class))).thenReturn(resena);

        // WHEN
        Resena resultado = resenaService.moderarResena(1L, "MODERADA");

        // THEN
        assertEquals("MODERADA", resultado.getEstado());
    }
}