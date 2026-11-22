package com.gamehub.authservice.config;

import com.gamehub.authservice.services.AuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "gamehub.auth.bootstrap-admin-email")
public class AdminInicialConfig {
    @Bean
    ApplicationRunner prepararAdmin(AuthService authService,
            @Value("${gamehub.auth.bootstrap-admin-email}") String email,
            @Value("${gamehub.auth.bootstrap-admin-password}") String password) {
        if (email.isBlank() || password.length() < 8) {
            throw new IllegalArgumentException("Configura el correo y la contraseña del administrador inicial.");
        }
        return args -> authService.prepararAdmin(email, password);
    }
}
