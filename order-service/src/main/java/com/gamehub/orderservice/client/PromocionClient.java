package com.gamehub.orderservice.client;

import com.gamehub.orderservice.model.DTOs.PromocionClientDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "promotion-service", url = "http://localhost:8084/api/promotions")
public interface PromocionClient {
    @GetMapping("/codigo/{codigo}")
    PromocionClientDTO obtenerPorCodigo(@PathVariable("codigo") String codigo);
}