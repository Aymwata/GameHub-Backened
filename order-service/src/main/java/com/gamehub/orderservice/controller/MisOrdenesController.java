package com.gamehub.orderservice.controller;

import com.gamehub.orderservice.model.DTOs.DetalleOrdenRequestDTO;
import com.gamehub.orderservice.model.DTOs.OrdenRequestDTO;
import com.gamehub.orderservice.model.DatosCheckout;
import com.gamehub.orderservice.model.Orden;
import com.gamehub.orderservice.service.OrdenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders/me")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class MisOrdenesController {
    private final OrdenService ordenService;

    @Operation(summary = "Listar las órdenes del cliente autenticado")
    @GetMapping
    public List<Orden> listar(@AuthenticationPrincipal Jwt token) {
        return ordenService.listarOrdenes(usuarioId(token), null);
    }

    @Operation(summary = "Consultar una orden propia del cliente autenticado")
    @GetMapping("/{id}")
    public ResponseEntity<Orden> buscar(@AuthenticationPrincipal Jwt token, @PathVariable Long id) {
        return ordenService.buscarPropia(id, usuarioId(token))
                .map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Crear una orden para el cliente autenticado")
    @PostMapping
    public ResponseEntity<Orden> crear(@AuthenticationPrincipal Jwt token,
                                       @Valid @RequestBody OrdenPropiaRequest compra) {
        OrdenRequestDTO request = new OrdenRequestDTO();
        request.setUsuarioId(usuarioId(token));
        request.setCodigoPromocion(compra.codigoPromocion());
        request.setDatosCheckout(compra.datosCheckout());
        request.setDetalles(compra.detalles());
        return ResponseEntity.status(HttpStatus.CREATED).body(ordenService.crearOrden(request));
    }

    private static Long usuarioId(Jwt token) {
        return Long.valueOf(token.getSubject());
    }

    public record OrdenPropiaRequest(String codigoPromocion, @Valid DatosCheckout datosCheckout,
            @NotEmpty List<@Valid @NotNull DetalleOrdenRequestDTO> detalles) {}
}
