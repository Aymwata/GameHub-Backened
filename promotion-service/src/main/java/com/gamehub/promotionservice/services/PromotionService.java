package com.gamehub.promotionservice.services;

import com.gamehub.promotionservice.client.CategoryClient;
import com.gamehub.promotionservice.client.ProductClient;
import com.gamehub.promotionservice.models.dto.CategoriaClientDTO;
import com.gamehub.promotionservice.models.dto.ProductoClientDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PromotionService {

    // Inyectamos nuestros dos clientes Feign
    private final CategoryClient categoryClient;
    private final ProductClient productClient;

    // Métodos de prueba para verificar que Feign logra traerse los datos
    public CategoriaClientDTO probarConexionCategoria(Long id) {
        return categoryClient.obtenerCategoria(id);
    }

    public ProductoClientDTO probarConexionProducto(Long id) {
        return productClient.obtenerProducto(id);
    }
}