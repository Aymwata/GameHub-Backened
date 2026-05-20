package com.gamehub.orderservice.client;

import com.gamehub.orderservice.model.DTOs.UsuarioClientDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "http://localhost:8080/api/usuarios")
public interface UsuarioClient {
    @GetMapping("/{id}")
    UsuarioClientDTO obtenerUsuario(@PathVariable("id") Long id);
}