package com.gamehub.authservice.Services;

import com.gamehub.authservice.Client.UserClient;
import com.gamehub.authservice.Models.CuentaAcceso;
import com.gamehub.authservice.Models.Dtos.CuentaAccesoRequestDTO;
import com.gamehub.authservice.Models.Dtos.LoginRequestDTO;
import com.gamehub.authservice.Repositories.CuentaAccesoRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final CuentaAccesoRepository cuentaRepository;
    private final UserClient userClient;

    public CuentaAcceso crearCuenta(CuentaAccesoRequestDTO dto) {
        log.info("Iniciando creación de cuenta de acceso para email: {}", dto.getEmail());

        // 1. Comunicación Remota: Validar si el usuario existe en user-service
        try {
            log.info("Consultando al user-service por el email: {}", dto.getEmail());
            userClient.buscarPorEmail(dto.getEmail()); // Si falla o retorna 404, lanzará una excepción
        } catch (FeignException.NotFound e) {
            log.error("El usuario con email {} no existe en el user-service", dto.getEmail());
            throw new IllegalArgumentException("Debe crear un perfil de usuario antes de crear una cuenta de acceso.");
        } catch (FeignException e) {
            log.error("Error de comunicación con user-service: {}", e.getMessage());
            throw new RuntimeException("Error interno al comunicar con user-service.");
        }

        // 2. Regla local: Validar que la cuenta de acceso no exista previamente
        if (cuentaRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("La cuenta de acceso ya se encuentra registrada.");
        }

        // 3. Crear la cuenta
        CuentaAcceso cuenta = new CuentaAcceso();
        cuenta.setEmail(dto.getEmail());
        cuenta.setPassword(dto.getPassword());
        cuenta.setRol(dto.getRol());
        cuenta.setEstado(true);

        CuentaAcceso guardado = cuentaRepository.save(cuenta);
        log.info("Cuenta de acceso creada con éxito. ID: {}", guardado.getId());

        return guardado;
    }

    public List<CuentaAcceso> listarCuentas() {
        return cuentaRepository.findAll();
    }

    public CuentaAcceso buscarPorId(Long id) {
        return cuentaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada."));
    }

    public CuentaAcceso buscarPorEmail(String email) {
        return cuentaRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada con ese email."));
    }

    public CuentaAcceso actualizarCuenta(Long id, CuentaAccesoRequestDTO dto) {
        log.info("Actualizando cuenta ID: {}", id);
        CuentaAcceso cuenta = buscarPorId(id);

        cuenta.setPassword(dto.getPassword());
        cuenta.setRol(dto.getRol());
        return cuentaRepository.save(cuenta);
    }

    public CuentaAcceso desactivarCuenta(Long id) {
        log.info("Desactivando cuenta ID: {}", id);
        CuentaAcceso cuenta = buscarPorId(id);
        cuenta.setEstado(false);
        return cuentaRepository.save(cuenta);
    }

    // Login simulado sin JWT
    public String simularLogin(LoginRequestDTO dto) {
        log.info("Intento de login para email: {}", dto.getEmail());

        CuentaAcceso cuenta = buscarPorEmail(dto.getEmail());

        if (!cuenta.getPassword().equals(dto.getPassword())) {
            throw new IllegalArgumentException("Credenciales inválidas.");
        }

        // Regla: Usuario inactivo no puede autenticarse
        if (!cuenta.getEstado()) {
            throw new IllegalArgumentException("La cuenta se encuentra inactiva.");
        }

        return "Login exitoso. Bienvenido " + cuenta.getRol();
    }
}