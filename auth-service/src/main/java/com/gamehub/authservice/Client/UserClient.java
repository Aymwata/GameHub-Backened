package com.gamehub.authservice.Client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

// Le indicamos la URL donde está corriendo el user-service
@FeignClient(name = "user-service", url = "http://localhost:8082/api/usuarios")
public interface UserClient {

    // Espejamos el endpoint que busca usuarios en el user-service
    // Nota: Como no necesitamos toda la entidad, podemos usar Object o un DTO simple
    @GetMapping("/buscar")
    Object buscarPorEmail(@RequestParam("email") String email);
}

