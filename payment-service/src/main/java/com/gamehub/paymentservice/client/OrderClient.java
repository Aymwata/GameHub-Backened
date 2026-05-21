package com.gamehub.paymentservice.client;

import com.gamehub.paymentservice.models.dto.OrdenClientDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "order-service", url = "http://localhost:8086")
public interface OrderClient {

    @GetMapping("/api/orders/{id}")
    OrdenClientDTO getOrdenById(@PathVariable("id") Long id);

    @PutMapping("/api/orders/{id}/status")
    void actualizarEstadoOrden(@PathVariable("id") Long id, @RequestParam("estado") String estado);
}