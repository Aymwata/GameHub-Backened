package com.gamehub.promotionservice;

import com.gamehub.promotionservice.client.CategoryClient;
import com.gamehub.promotionservice.client.ProductClient;
import com.gamehub.promotionservice.models.Promotion;
import com.gamehub.promotionservice.models.dto.PromotionRequestDTO;
import com.gamehub.promotionservice.models.dto.PromotionResponseDTO;
import com.gamehub.promotionservice.repositories.PromotionRepository;
import com.gamehub.promotionservice.services.PromotionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PromotionServiceTest {

    @Mock
    private PromotionRepository promotionRepository;

    @Mock
    private CategoryClient categoryClient;

    @Mock
    private ProductClient productClient;

    @InjectMocks
    private PromotionService promotionService;

    @Test
    @DisplayName("Test: Crear promoción exitosamente")
    void crearPromocionExitosa() {
        PromotionRequestDTO request = new PromotionRequestDTO();
        request.setCodigo("GAMER20");
        request.setTipo("PORCENTAJE");
        request.setValor(20.0);
        request.setFechaInicio(LocalDateTime.now());
        request.setFechaFin(LocalDateTime.now().plusDays(10));

        Promotion promoGuardada = new Promotion();
        promoGuardada.setId(1L);
        promoGuardada.setCodigo("GAMER20");
        promoGuardada.setTipo("PORCENTAJE");
        promoGuardada.setValor(20.0);
        promoGuardada.setEstado(true);

        when(promotionRepository.findByCodigo("GAMER20")).thenReturn(Optional.empty());
        when(promotionRepository.save(any(Promotion.class))).thenReturn(promoGuardada);

        PromotionResponseDTO resultado = promotionService.crearPromocion(request);

        assertNotNull(resultado);
        assertEquals("GAMER20", resultado.getCodigo());
        assertEquals(20.0, resultado.getValor());
        verify(promotionRepository, times(1)).save(any(Promotion.class));
    }

    @Test
    @DisplayName("Test: Fallo al crear promoción por fechas ilógicas")
    void crearPromocionFalloFechas() {
        PromotionRequestDTO request = new PromotionRequestDTO();
        request.setCodigo("GAMER20");
        request.setFechaInicio(LocalDateTime.now().plusDays(10));
        request.setFechaFin(LocalDateTime.now());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            promotionService.crearPromocion(request);
        });

        assertTrue(exception.getMessage().contains("La fecha de inicio no puede ser posterior"));
        verify(promotionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Test: Desactivar promoción exitosamente")
    void desactivarPromocionExitosa() {
        Promotion promoExistente = new Promotion();
        promoExistente.setId(1L);
        promoExistente.setEstado(true);

        when(promotionRepository.findById(1L)).thenReturn(Optional.of(promoExistente));
        when(promotionRepository.save(any(Promotion.class))).thenReturn(promoExistente);

        promotionService.desactivarPromocion(1L);

        assertFalse(promoExistente.getEstado());
        verify(promotionRepository, times(1)).save(promoExistente);
    }
}