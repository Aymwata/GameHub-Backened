package com.gamehub.promotionservice;

import com.gamehub.promotionservice.client.CategoryClient;
import com.gamehub.promotionservice.client.ProductClient;
import com.gamehub.promotionservice.models.Promotion;
import com.gamehub.promotionservice.models.dto.PromotionResponseDTO;
import com.gamehub.promotionservice.repositories.PromotionRepository;
import com.gamehub.promotionservice.services.PromotionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PromotionServiceTest {

    // 1. Declaramos TODOS los componentes que inyecta tu PromotionService
    @Mock
    private PromotionRepository repository;

    @Mock
    private CategoryClient categoryClient;

    @Mock
    private ProductClient productClient;

    @InjectMocks
    private PromotionService promotionService;

    @Test
    @DisplayName("Debería retornar el DTO de la promoción si el código de cupón existe y está activo")
    void testObtenerPorCodigoExitosamente() {
        // 1. GIVEN: Configuración del escenario simulado
        Promotion promoMock = new Promotion();
        promoMock.setId(1L);
        promoMock.setCodigo("GAMER20");
        promoMock.setValor(20.0); // Usando el atributo real de tu entidad
        promoMock.setEstado(true); // Usando el atributo real de tu entidad

        // Dejamos productoId y categoriaId nulos para no forzar la llamada a los FeignClients en este test básico
        promoMock.setProductoId(null);
        promoMock.setCategoriaId(null);

        // Simulamos que el repositorio encuentra la promoción
        Mockito.when(repository.findByCodigo("GAMER20")).thenReturn(Optional.of(promoMock));

        // 2. WHEN: Ejecución del método real
        PromotionResponseDTO resultado = promotionService.obtenerPorCodigo("GAMER20");

        // 3. THEN: Validaciones
        assertNotNull(resultado, "El DTO de respuesta no debería ser nulo");
        assertEquals("GAMER20", resultado.getCodigo(), "El código del cupón en el DTO debe ser GAMER20");
        assertEquals(20.0, resultado.getValor(), "El valor del descuento debe ser el esperado");
        assertTrue(resultado.getEstado(), "El estado en el DTO debe ser true (activo)");

        // Verificamos que se consultó a la base de datos exactamente 1 vez
        Mockito.verify(repository, Mockito.times(1)).findByCodigo("GAMER20");
    }

    @Test
    @DisplayName("Debería lanzar una excepción si la promoción está inactiva")
    void testObtenerPorCodigoInactivo() {
        // 1. GIVEN
        Promotion promoInactiva = new Promotion();
        promoInactiva.setCodigo("GAMER20");
        promoInactiva.setEstado(false); // Estado inactivo para provocar la validación

        Mockito.when(repository.findByCodigo("GAMER20")).thenReturn(Optional.of(promoInactiva));

        // 2. WHEN & THEN: Verificamos que salte tu RuntimeException exacta
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            promotionService.obtenerPorCodigo("GAMER20");
        });

        assertEquals("Error: Este código de promoción se encuentra inactivo.", exception.getMessage());
    }
}