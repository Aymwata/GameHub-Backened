package com.gamehub.authservice.services;


import com.gamehub.authservice.client.UserClient;
import com.gamehub.authservice.models.CuentaAcceso;
import com.gamehub.authservice.models.Dtos.CuentaAccesoRequestDTO;
import com.gamehub.authservice.models.Dtos.LoginRequestDTO;
import com.gamehub.authservice.repositories.CuentaAccesoRepository;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;



@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    // MOCKS
    @Mock
    private CuentaAccesoRepository cuentaRepository;

    @Mock
    private UserClient userClient;

    // TEST
    @InjectMocks
    private AuthService authService;


    private CuentaAccesoRequestDTO cuentaDto;
    private CuentaAcceso cuenta;
    private LoginRequestDTO loginDto;


    // Se ejecutra antes de cada @Test
    @BeforeEach
    void setUp() {
        // DTO simulado
        cuentaDto = new CuentaAccesoRequestDTO();
        cuentaDto.setEmail("jugador@gamehub.com");
        cuentaDto.setPassword("secreto123");
        cuentaDto.setRol("CLIENTE");

        // entidad simulada
        cuenta = new CuentaAcceso();
        cuenta.setId(1L);
        cuenta.setEmail("jugador@gamehub.com");
        cuenta.setPassword("secreto123");
        cuenta.setRol("CLIENTE");
        cuenta.setEstado(true);

        // DTO de Login simulado
        loginDto = new LoginRequestDTO();
        loginDto.setEmail("jugador@gamehub.com");
        loginDto.setPassword("secreto123");
    }

    //PRUEBAS PARA CREACIÓN DE CUENTA

    @Test
    void crearCuenta_exitosamente() {
        // GIVEN
        doNothing().when(userClient).buscarPorEmail(anyString());
        // Simulamos que la cuenta on existe
        when(cuentaRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        // Simula el guardado
        when(cuentaRepository.save(any(CuentaAcceso.class))).thenReturn(cuenta);

        // WHEN
        CuentaAcceso resultado = authService.crearCuenta(cuentaDto);

        // THEN
        assertNotNull(resultado, "El resultado no debería ser nulo");
        assertEquals("jugador@gamehub.com", resultado.getEmail(), "El email debe coincidir");
        assertTrue(resultado.getEstado(), "La cuenta debe crearse con estado activo");
        verify(cuentaRepository, times(1)).save(any(CuentaAcceso.class));
    }

    @Test
    void crearCuenta_fallaPorqueUsuarioNoExisteEnUserService() {
        // GIVEN
        // Simula que el UserClient lanza un error 404
        FeignException.NotFound feignExceptionMock = mock(FeignException.NotFound.class);
        doThrow(feignExceptionMock).when(userClient).buscarPorEmail(anyString());

        // WHEN Y THEN
        // Verificamos que se lance la excepcion correcta con el mensaje esperado
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.crearCuenta(cuentaDto);
        });

        assertEquals("Debe crear un perfil de usuario antes de crear una cuenta de acceso.", exception.getMessage());
        // Verificamos que no se haya llamado a la BD para guardar
        verify(cuentaRepository, never()).save(any(CuentaAcceso.class));
    }

    @Test
    void crearCuenta_fallaPorqueEmailYaExiste() {
        // GIVEN
        doNothing().when(userClient).buscarPorEmail(anyString());
        // Simulamos que la BD ya tiene una cuenta con ese correo
        when(cuentaRepository.findByEmail(anyString())).thenReturn(Optional.of(cuenta));

        // WHEN Y THEN
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.crearCuenta(cuentaDto);
        });

        assertEquals("La cuenta de acceso ya se encuentra registrada.", exception.getMessage());
    }

    //PRUEBAS PARA BÚSQUEDA

    @Test
    void buscarPorId_exitoso() {
        // GIVEN
        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuenta));

        // WHEN
        CuentaAcceso resultado = authService.buscarPorId(1L);

        // THEN
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
    }

    @Test
    void buscarPorId_noEncontrado() {
        // GIVEN
        when(cuentaRepository.findById(99L)).thenReturn(Optional.empty());

        // WHEN Y THEN
        assertThrows(IllegalArgumentException.class, () -> {
            authService.buscarPorId(99L);
        });
    }

    //PRUEBAS PARA LOGIN

    @Test
    void simularLogin_exitoso() {
        // GIVEN
        when(cuentaRepository.findByEmail(anyString())).thenReturn(Optional.of(cuenta));

        // WHEN
        String mensaje = authService.simularLogin(loginDto);

        // THEN
        assertEquals("Login exitoso. Bienvenido CLIENTE", mensaje);
    }

    @Test
    void simularLogin_fallaPorContrasenaIncorrecta() {
        // GIVEN
        loginDto.setPassword("contraseñaMala");
        when(cuentaRepository.findByEmail(anyString())).thenReturn(Optional.of(cuenta));

        // WHEN Y THEN
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.simularLogin(loginDto);
        });

        assertEquals("Credenciales inválidas.", exception.getMessage());
    }

    @Test
    void simularLogin_fallaPorCuentaInactiva() {
        // GIVEN
        cuenta.setEstado(false);
        when(cuentaRepository.findByEmail(anyString())).thenReturn(Optional.of(cuenta));

        // WHEN Y THEN
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.simularLogin(loginDto);
        });

        assertEquals("La cuenta se encuentra inactiva.", exception.getMessage());
    }
}