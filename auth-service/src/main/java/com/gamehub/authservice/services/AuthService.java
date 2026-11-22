package com.gamehub.authservice.services;

import com.gamehub.authservice.client.UserClient;
import com.gamehub.authservice.client.UserClient.UsuarioPerfil;
import com.gamehub.authservice.config.AuthSecurityConfig;
import com.gamehub.authservice.models.CuentaAcceso;
import com.gamehub.authservice.models.Dtos.CuentaAccesoRequestDTO;
import com.gamehub.authservice.models.Dtos.LoginRequestDTO;
import com.gamehub.authservice.models.Dtos.RegistroRequestDTO;
import com.gamehub.authservice.models.SesionRefresh;
import com.gamehub.authservice.repositories.CuentaAccesoRepository;
import com.gamehub.authservice.repositories.SesionRefreshRepository;
import feign.FeignException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {
    private static final Duration ACCESS_TTL = Duration.ofMinutes(10);
    private static final Duration REFRESH_TTL = Duration.ofDays(7);
    private static final SecureRandom RANDOM = new SecureRandom();

    private final CuentaAccesoRepository cuentaRepository;
    private final UserClient userClient;
    private final SesionRefreshRepository sesiones;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;
    private final TransactionTemplate transactions;

    public UsuarioSesion registrar(RegistroRequestDTO dto) {
        String email = normalizarEmail(dto.email());
        CuentaAcceso pendiente;
        try {
            pendiente = reservarRegistro(email, dto.password());
        } catch (DataIntegrityViolationException e) {
            // Another request can reserve the same email between lookup and insert.
            pendiente = reservarRegistro(email, dto.password());
        }

        UsuarioPerfil perfil;
        try {
            perfil = userClient.registrarCliente(pendiente.getRegistroId(),
                    new UserClient.NuevoUsuario(dto.nombre(), email, dto.telefono(), "CLIENTE"));
        } catch (FeignException.Conflict e) {
            Long cuentaId = pendiente.getId();
            transactions.executeWithoutResult(status -> cuentaRepository.bloquearPorId(cuentaId)
                    .filter(cuenta -> cuenta.getUsuarioId() == null)
                    .ifPresent(cuentaRepository::delete));
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El email ya se encuentra registrado.");
        } catch (FeignException e) {
            // The reservation lets the browser retry safely if the remote response was lost.
            throw perfilesNoDisponibles();
        }

        Long cuentaId = pendiente.getId();
        return transactions.execute(status -> {
            CuentaAcceso cuenta = cuentaRepository.bloquearPorId(cuentaId).orElseThrow();
            cuenta.setUsuarioId(perfil.id());
            cuenta.setEstado(true);
            return usuarioSesion(cuenta, perfil);
        });
    }

    private CuentaAcceso reservarRegistro(String email, String password) {
        return transactions.execute(status -> {
            var existente = cuentaRepository.findByEmailIgnoreCase(email);
            if (existente.isPresent()) {
                CuentaAcceso cuenta = existente.get();
                if (cuenta.getRegistroId() == null || cuenta.getUsuarioId() != null ||
                        !passwordEncoder.matches(password, cuenta.getPassword())) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "El email ya se encuentra registrado.");
                }
                return cuenta;
            }
            CuentaAcceso cuenta = nuevaCuenta(email, password, "CLIENTE");
            cuenta.setRegistroId(UUID.randomUUID());
            cuenta.setEstado(false);
            return cuentaRepository.saveAndFlush(cuenta);
        });
    }

    @Transactional
    public CuentaAcceso crearCuenta(CuentaAccesoRequestDTO dto) {
        if (cuentaRepository.findByEmailIgnoreCase(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("La cuenta de acceso ya se encuentra registrada.");
        }
        UsuarioPerfil perfil = buscarPerfil(dto.getEmail(), new IllegalArgumentException(
                "Debe crear un perfil de usuario antes de crear una cuenta de acceso."));
        CuentaAcceso cuenta = nuevaCuenta(perfil.email(), dto.getPassword(), normalizarRol(dto.getRol()));
        cuenta.setUsuarioId(perfil.id());
        return cuentaRepository.save(cuenta);
    }

    @Transactional
    public void prepararAdmin(String email, String password) {
        if (cuentaRepository.findByEmailIgnoreCase(email).isEmpty()) {
            cuentaRepository.save(nuevaCuenta(normalizarEmail(email), password, "ADMIN"));
        }
    }

    private CuentaAcceso nuevaCuenta(String email, String password, String rol) {
        CuentaAcceso cuenta = new CuentaAcceso();
        cuenta.setEmail(email);
        cuenta.setPassword(passwordEncoder.encode(password));
        cuenta.setRol(rol);
        cuenta.setEstado(true);
        return cuenta;
    }

    public List<CuentaAcceso> listarCuentas() {
        return cuentaRepository.findAll();
    }

    public CuentaAcceso buscarPorId(Long id) {
        return cuentaRepository.findById(id).orElseThrow(AuthService::cuentaNoEncontrada);
    }

    public CuentaAcceso buscarPorEmail(String email) {
        return cuentaRepository.findByEmailIgnoreCase(email).orElseThrow(AuthService::cuentaNoEncontrada);
    }

    @Transactional
    public CuentaAcceso actualizarCuenta(Long id, CuentaAccesoRequestDTO dto) {
        CuentaAcceso cuenta = cuentaRepository.bloquearPorId(id).orElseThrow(AuthService::cuentaNoEncontrada);
        cuenta.setPassword(passwordEncoder.encode(dto.getPassword()));
        cuenta.setRol(normalizarRol(dto.getRol()));
        sesiones.deleteAllByCuentaId(id);
        return cuenta;
    }

    @Transactional
    public CuentaAcceso desactivarCuenta(Long id) {
        CuentaAcceso cuenta = cuentaRepository.bloquearPorId(id).orElseThrow(AuthService::cuentaNoEncontrada);
        cuenta.setEstado(false);
        sesiones.deleteAllByCuentaId(id);
        return cuenta;
    }

    @Transactional
    public Sesion login(LoginRequestDTO dto) {
        CuentaAcceso encontrada = cuentaRepository.findByEmailIgnoreCase(dto.getEmail().strip())
                .orElseThrow(AuthService::credencialesInvalidas);
        CuentaAcceso cuenta = cuentaRepository.bloquearPorId(encontrada.getId())
                .orElseThrow(AuthService::credencialesInvalidas);
        boolean passwordAnterior = !cuenta.getPassword().startsWith(AuthSecurityConfig.PASSWORD_PREFIX);
        boolean coincide = passwordAnterior
                ? MessageDigest.isEqual(bytes(cuenta.getPassword()), bytes(dto.getPassword()))
                : passwordEncoder.matches(dto.getPassword(), cuenta.getPassword());
        if (!Boolean.TRUE.equals(cuenta.getEstado()) || !coincide) {
            throw credencialesInvalidas();
        }
        if (!dto.getPerfil().name().equalsIgnoreCase(cuenta.getRol())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "La cuenta no tiene acceso a este perfil.");
        }

        UsuarioPerfil perfil = perfilActivo(cuenta);
        if (passwordAnterior) {
            cuenta.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        cuenta.setUsuarioId(perfil.id());
        sesiones.deleteAllByCuentaIdAndVenceBefore(cuenta.getId(), Instant.now());
        return emitir(cuenta, perfil, Instant.now().plus(REFRESH_TTL));
    }

    @Transactional
    public Sesion renovar(String token) {
        String hash = hashRefresh(token);
        SesionRefresh sesion = sesiones.findById(hash).orElseThrow(AuthService::sesionInvalida);
        CuentaAcceso cuenta = cuentaRepository.bloquearPorId(sesion.getCuentaId())
                .orElseThrow(AuthService::sesionInvalida);
        if (!sesion.getVence().isAfter(Instant.now()) || !Boolean.TRUE.equals(cuenta.getEstado()) ||
                sesiones.consumir(hash) != 1) {
            throw sesionInvalida();
        }
        UsuarioPerfil perfil = perfilActivo(cuenta);
        return emitir(cuenta, perfil, sesion.getVence());
    }

    @Transactional
    public void cerrarSesion(String token) {
        if (token != null && token.length() == 43) {
            sesiones.consumir(hashRefresh(token));
        }
    }

    private Sesion emitir(CuentaAcceso cuenta, UsuarioPerfil perfil, Instant venceRefresh) {
        Instant ahora = Instant.now();
        UsuarioSesion usuario = usuarioSesion(cuenta, perfil);
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(AuthSecurityConfig.ISSUER)
                .audience(List.of(AuthSecurityConfig.AUDIENCE))
                .subject(perfil.id().toString())
                .id(UUID.randomUUID().toString())
                .issuedAt(ahora)
                .expiresAt(ahora.plus(ACCESS_TTL))
                .claim("roles", List.of(usuario.rol()))
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        String accessToken = jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
        byte[] random = new byte[32];
        RANDOM.nextBytes(random);
        String refresh = Base64.getUrlEncoder().withoutPadding().encodeToString(random);
        sesiones.save(new SesionRefresh(hashRefresh(refresh), cuenta.getId(), venceRefresh));
        return new Sesion(new Acceso(accessToken, ACCESS_TTL.toSeconds(), usuario), refresh, venceRefresh);
    }

    private UsuarioPerfil perfilActivo(CuentaAcceso cuenta) {
        UsuarioPerfil perfil = buscarPerfil(cuenta.getEmail(), credencialesInvalidas());
        if (!Boolean.TRUE.equals(perfil.estado()) ||
                (cuenta.getUsuarioId() != null && !cuenta.getUsuarioId().equals(perfil.id()))) {
            throw credencialesInvalidas();
        }
        return perfil;
    }

    private UsuarioPerfil buscarPerfil(String email, RuntimeException noEncontrado) {
        try {
            return userClient.buscarPorEmail(email);
        } catch (FeignException.NotFound | FeignException.Conflict e) {
            throw noEncontrado;
        } catch (FeignException e) {
            throw perfilesNoDisponibles();
        }
    }

    private static UsuarioSesion usuarioSesion(CuentaAcceso cuenta, UsuarioPerfil perfil) {
        return new UsuarioSesion(perfil.id(), perfil.nombre(), perfil.email(), normalizarRol(cuenta.getRol()));
    }

    private static String normalizarEmail(String email) {
        return email.strip().toLowerCase(Locale.ROOT);
    }

    private static String normalizarRol(String rol) {
        String normalizado = rol.strip().toUpperCase(Locale.ROOT);
        if (!Set.of("CLIENTE", "ADMIN", "OPERADOR").contains(normalizado)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rol desconocido.");
        }
        return normalizado;
    }

    private static byte[] bytes(String valor) {
        return valor.getBytes(StandardCharsets.UTF_8);
    }

    private static String hashRefresh(String token) {
        if (token == null || token.length() != 43) {
            throw sesionInvalida();
        }
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes(token)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    private static ResponseStatusException cuentaNoEncontrada() {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Cuenta no encontrada.");
    }

    private static ResponseStatusException credencialesInvalidas() {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas.");
    }

    private static ResponseStatusException sesionInvalida() {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sesión vencida o inválida.");
    }

    private static ResponseStatusException perfilesNoDisponibles() {
        return new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Servicio de usuarios no disponible.");
    }

    public record UsuarioSesion(Long id, String nombre, String email, String rol) {}
    public record Acceso(String accessToken, long expiresIn, UsuarioSesion usuario) {}
    public record Sesion(Acceso acceso, String refreshToken, Instant venceRefresh) {}
}
