package com.gamehub.shippingservice.client;

import com.gamehub.shippingservice.models.dto.OrderClientDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "order-service", url = "http://localhost:8086")
public interface OrderClient {

    // Llama al microservicio de órdenes para ver el estado de la compra
    @GetMapping("/api/orders/{id}")
    OrderClientDTO obtenerOrdenPorId(@PathVariable("id") Long id);
}
