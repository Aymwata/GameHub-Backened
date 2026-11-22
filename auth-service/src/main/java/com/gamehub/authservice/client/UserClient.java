package com.gamehub.authservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.UUID;

//URL donde está corriendo el user-service
@FeignClient(name = "user-service", url = "${gamehub.users.url:http://localhost:8080/api/users}")
public interface UserClient {

    @GetMapping("/buscar")
    UsuarioPerfil buscarPorEmail(@RequestParam("email") String email);

    @PostMapping
    UsuarioPerfil registrarCliente(@RequestHeader("X-GameHub-Registration") UUID registroId,
                                   @RequestBody NuevoUsuario usuario);

    record UsuarioPerfil(Long id, String nombre, String email, Boolean estado) {}

    record NuevoUsuario(String nombre, String email, String telefono, String rol) {}
}
