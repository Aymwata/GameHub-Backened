package com.gamehub.orderservice.controller;

import com.gamehub.orderservice.model.DTOs.DetalleOrdenRequestDTO;
import com.gamehub.orderservice.model.DTOs.OrdenRequestDTO;
import com.gamehub.orderservice.model.Orden;
import com.gamehub.orderservice.service.OrdenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
        request.setNombreContacto(compra.nombreContacto());
        request.setCorreoContacto(compra.correoContacto());
        request.setTelefonoContacto(compra.telefonoContacto());
        request.setRegion(compra.region());
        request.setCiudad(compra.ciudad());
        request.setComuna(compra.comuna());
        request.setCalle(compra.calle());
        request.setNumero(compra.numero());
        request.setDetalles(compra.detalles());
        return ResponseEntity.status(HttpStatus.CREATED).body(ordenService.crearOrden(request));
    }

    private static Long usuarioId(Jwt token) {
        return Long.valueOf(token.getSubject());
    }

    public record OrdenPropiaRequest(String codigoPromocion,
            @NotBlank @Size(max = 100) String nombreContacto,
            @NotBlank @Email @Size(max = 100) String correoContacto,
            @NotBlank @Pattern(regexp = "\\d{9}") String telefonoContacto,
            @NotBlank @Size(max = 100) String region,
            @NotBlank @Size(max = 100) String ciudad,
            @NotBlank @Size(max = 100) String comuna,
            @NotBlank @Size(max = 150) String calle,
            @NotBlank @Size(max = 20) String numero,
            @NotEmpty List<@Valid @NotNull DetalleOrdenRequestDTO> detalles) {}
}

