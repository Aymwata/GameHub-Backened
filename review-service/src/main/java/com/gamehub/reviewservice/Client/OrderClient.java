package com.gamehub.reviewservice.Client;

import com.gamehub.reviewservice.Models.DTOs.OrdenDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "order-service", url = "http://localhost:8086/api/orders")
public interface OrderClient {
    @GetMapping("/{id}")
    OrdenDTO buscarPorId(@PathVariable("id") Long id);
}