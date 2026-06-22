package com.gamehub.gatewayservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration
public class SecurityConfig {

    // Lee la clave secreta que pusimos en el application.yml
    @Value("${jwt.secret}")
    private String secret;

    // Motor que desencripta y verifica que el token no sea falso ni esté caducado
    @Bean
    public JwtDecoder jwtDecoder() {
        SecretKey key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(key).macAlgorithm(MacAlgorithm.HS256).build();
    }

    // Extrae los roles del usuario desde el interior del token
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter authorities = new JwtGrantedAuthoritiesConverter();
        authorities.setAuthoritiesClaimName("roles");
        authorities.setAuthorityPrefix("");
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(authorities);
        return converter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationConverter converter) throws Exception {
        http
                // Desactiva CSRF porque los microservicios REST no usan formularios tradicionales
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // 1. EL LOGIN DEBE SER PÚBLICO: Nadie tiene token antes de iniciar sesión
                        .requestMatchers("/api/auth/**").permitAll()
                        // 2. SWAGGER DEBE SER PÚBLICO: Dejamos pasar todo lo relacionado a la documentación
                        .requestMatchers("/docs/**", "/swagger-ui/**", "/v3/api-docs/**", "/api-docs/**", "/webjars/**").permitAll()
                        // 3. EL RESTO SE BLOQUEA: Cualquier otra ruta a productos, envíos, etc., exigirá Token
                        .anyRequest().authenticated())
                // Política STATELESS: El Gateway no recuerda a nadie, cada petición debe traer su propio token
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Activa la lectura del JWT
                .oauth2ResourceServer(oauth -> oauth.jwt(jwt -> jwt.jwtAuthenticationConverter(converter)));
        return http.build();
    }
}