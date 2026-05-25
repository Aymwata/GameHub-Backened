package com.gamehub.shippingservice.client;

import com.gamehub.shippingservice.models.dto.AddressClientDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "user-service", url = "http://localhost:8080")
public interface UserClient {

    @GetMapping("/api/directions/usuario/{userId}")
    List<AddressClientDTO> obtenerDireccionDelUsuario(@PathVariable("userId") Long userId);
}
