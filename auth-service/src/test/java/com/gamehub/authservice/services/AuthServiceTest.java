package com.gamehub.authservice.services;

import com.gamehub.authservice.client.UserClient;
import com.gamehub.authservice.config.AuthSecurityConfig;
import com.gamehub.authservice.models.CuentaAcceso;
import com.gamehub.authservice.models.Dtos.CuentaAccesoRequestDTO;
import com.gamehub.authservice.models.Dtos.LoginRequestDTO;
import com.gamehub.authservice.repositories.CuentaAccesoRepository;
import com.gamehub.authservice.repositories.SesionRefreshRepository;
import feign.FeignException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    private static final String EMAIL = "jugador@gamehub.example";
    private static final String PASSWORD = "secreto123";
    private static final String HASH = AuthSecurityConfig.PASSWORD_PREFIX + "hash-de-prueba";

    @Mock private CuentaAccesoRepository cuentas;
    @Mock private UserClient usuarios;
    @Mock private SesionRefreshRepository sesiones;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtEncoder jwtEncoder;
    @InjectMocks private AuthService authService;

    private CuentaAcceso cuenta;
    private CuentaAccesoRequestDTO cuentaDto;
    private LoginRequestDTO loginDto;

    @BeforeEach
    void preparar() {
        cuentaDto = new CuentaAccesoRequestDTO();
        cuentaDto.setEmail(EMAIL);
        cuentaDto.setPassword(PASSWORD);
        cuentaDto.setRol("CLIENTE");

        cuenta = new CuentaAcceso();
        cuenta.setId(1L);
        cuenta.setEmail(EMAIL);
        cuenta.setPassword(HASH);
        cuenta.setRol("CLIENTE");
        cuenta.setEstado(true);

        loginDto = new LoginRequestDTO();
        loginDto.setEmail(EMAIL);
        loginDto.setPassword(PASSWORD);
        loginDto.setPerfil(LoginRequestDTO.Perfil.CLIENTE);
    }

    @Test
    void crearCuentaGuardaHashYVinculaPerfil() {
        when(usuarios.buscarPorEmail(EMAIL)).thenReturn(perfil());
        when(passwordEncoder.encode(PASSWORD)).thenReturn(HASH);
        when(cuentas.save(any(CuentaAcceso.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CuentaAcceso creada = authService.crearCuenta(cuentaDto);

        assertEquals(EMAIL, creada.getEmail());
        assertEquals(HASH, creada.getPassword());
        assertEquals(2L, creada.getUsuarioId());
        assertTrue(creada.getEstado());
    }

    @Test
    void crearCuentaRequierePerfilExistente() {
        when(usuarios.buscarPorEmail(EMAIL)).thenThrow(mock(FeignException.NotFound.class));

        assertThrows(IllegalArgumentException.class, () -> authService.crearCuenta(cuentaDto));
        verify(cuentas, never()).save(any());
    }

    @Test
    void crearCuentaRechazaEmailDuplicado() {
        when(cuentas.findByEmailIgnoreCase(EMAIL)).thenReturn(Optional.of(cuenta));

        assertThrows(IllegalArgumentException.class, () -> authService.crearCuenta(cuentaDto));
        verifyNoInteractions(usuarios);
    }

    @Test
    void buscarPorIdDevuelveCuentaExistente() {
        when(cuentas.findById(1L)).thenReturn(Optional.of(cuenta));

        assertEquals(1L, authService.buscarPorId(1L).getId());
    }

    @Test
    void buscarPorIdDevuelve404ParaCuentaInexistente() {
        assertEquals(HttpStatus.NOT_FOUND,
                assertThrows(ResponseStatusException.class, () -> authService.buscarPorId(99L)).getStatusCode());
    }

    @Test
    void loginMigraPasswordAnteriorYEmiteSesion() {
        prepararCuentaParaLogin();
        cuenta.setPassword(PASSWORD);
        when(usuarios.buscarPorEmail(EMAIL)).thenReturn(perfil());
        when(passwordEncoder.encode(PASSWORD)).thenReturn(HASH);
        when(jwtEncoder.encode(any())).thenReturn(Jwt.withTokenValue("jwt-de-prueba")
                .header("alg", "HS256").subject("2").build());

        AuthService.Sesion sesion = authService.login(loginDto);

        assertEquals(HASH, cuenta.getPassword());
        assertEquals(2L, sesion.acceso().usuario().id());
        assertEquals("CLIENTE", sesion.acceso().usuario().rol());
        assertEquals("jwt-de-prueba", sesion.acceso().accessToken());
        assertEquals(43, sesion.refreshToken().length());
        verify(sesiones).save(any());
    }

    @Test
    void loginRechazaPasswordIncorrecta() {
        prepararCuentaParaLogin();
        assertLoginError(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void loginRechazaCuentaInactiva() {
        prepararCuentaParaLogin();
        cuenta.setEstado(false);
        assertLoginError(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void loginRechazaVistaAdminParaUnClienteConPasswordValida() {
        prepararCuentaParaLogin();
        loginDto.setPerfil(LoginRequestDTO.Perfil.ADMIN);
        when(passwordEncoder.matches(PASSWORD, HASH)).thenReturn(true);
        assertLoginError(HttpStatus.FORBIDDEN);
    }

    private void prepararCuentaParaLogin() {
        when(cuentas.findByEmailIgnoreCase(EMAIL)).thenReturn(Optional.of(cuenta));
        when(cuentas.bloquearPorId(1L)).thenReturn(Optional.of(cuenta));
    }

    private void assertLoginError(HttpStatus estado) {
        assertEquals(estado,
                assertThrows(ResponseStatusException.class, () -> authService.login(loginDto)).getStatusCode());
        verifyNoInteractions(jwtEncoder, sesiones);
    }

    private UserClient.UsuarioPerfil perfil() {
        return new UserClient.UsuarioPerfil(2L, "Jugador", EMAIL, true);
    }
}
