package com.gamehub.gatewayservice;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"jwt.secret=gamehub-test-clave-ficticia-de-32-bytes",
        "eureka.client.enabled=false"})
class GatewayServiceApplicationTests {
    private static HttpServer autenticacion;

    @LocalServerPort
    private int puerto;

    @DynamicPropertySource
    static void configurarRuta(DynamicPropertyRegistry propiedades) throws IOException {
        autenticacion = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        autenticacion.createContext("/api/auth/login", solicitud -> {
            solicitud.getResponseHeaders().add("Set-Cookie", "gamehub_refresh=prueba; Path=/api/auth; HttpOnly");
            solicitud.sendResponseHeaders(200, -1);
            solicitud.close();
        });
        autenticacion.createContext("/api/auth/refresh", solicitud -> {
            String cookie = solicitud.getRequestHeaders().getFirst("Cookie");
            solicitud.sendResponseHeaders("gamehub_refresh=prueba".equals(cookie) ? 200 : 401, -1);
            solicitud.close();
        });
        autenticacion.start();
        String ruta = "spring.cloud.gateway.server.webmvc.routes[0]";
        propiedades.add(ruta + ".id", () -> "auth-service");
        propiedades.add(ruta + ".uri", () -> "http://127.0.0.1:" + autenticacion.getAddress().getPort());
        propiedades.add(ruta + ".predicates[0]", () -> "Path=/api/auth/**");
    }

    @AfterAll
    static void detenerAutenticacion() {
        if (autenticacion != null) autenticacion.stop(0);
    }

    @Test
    void aislaLasCookiesDeCadaSolicitud() throws Exception {
        try (HttpClient cliente = HttpClient.newHttpClient()) {
            var login = cliente.send(solicitud("login").build(), HttpResponse.BodyHandlers.discarding());
            assertEquals(200, login.statusCode());
            assertTrue(login.headers().firstValue("Set-Cookie").isPresent());

            var anonima = cliente.send(solicitud("refresh").build(), HttpResponse.BodyHandlers.discarding());
            assertEquals(401, anonima.statusCode());

            var autenticada = cliente.send(solicitud("refresh")
                    .header("Cookie", "gamehub_refresh=prueba").build(), HttpResponse.BodyHandlers.discarding());
            assertEquals(200, autenticada.statusCode());
        }
    }

    private HttpRequest.Builder solicitud(String operacion) {
        return HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + puerto + "/api/auth/" + operacion))
                .POST(HttpRequest.BodyPublishers.noBody());
    }

}
