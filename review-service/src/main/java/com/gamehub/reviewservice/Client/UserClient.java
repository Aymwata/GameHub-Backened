package com.gamehub.reviewservice.Client;

import com.gamehub.reviewservice.Models.DTOs.UsuarioDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// Ajusta el 'path' si tus controllers tienen una ruta base como /api/usuarios
@FeignClient(name = "user-service", url = "http://localhost:8080/api/users")
public interface UserClient {
    @GetMapping("/{id}")
    UsuarioDTO buscarPorId(@PathVariable("id") Long id);
}