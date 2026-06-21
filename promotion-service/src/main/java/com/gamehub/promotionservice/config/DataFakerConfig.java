package com.gamehub.promotionservice.config;

import com.gamehub.promotionservice.models.Promotion;
import com.gamehub.promotionservice.repositories.PromotionRepository;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Configuration
public class DataFakerConfig {

    @Bean
    CommandLineRunner initPromotionDatabase(PromotionRepository repository) {
        return args -> {
            Faker faker = new Faker();
            System.out.println("--- Generando cupones de Promoción (DataFaker) ---");

            for (int i = 0; i < 5; i++) {
                Promotion promo = new Promotion();
                promo.setCodigo("PROMO" + faker.number().digits(5) + i);
                promo.setTipo(faker.options().option("PORCENTAJE", "FIJO"));
                promo.setValor(faker.number().randomDouble(2, 10, 50));

                promo.setFechaInicio(LocalDateTime.now().minusDays(faker.number().numberBetween(1, 5)));
                promo.setFechaFin(LocalDateTime.now().plusDays(faker.number().numberBetween(10, 30)));

                promo.setMontoMinimo(faker.number().randomDouble(2, 5000, 20000));
                promo.setUsosMaximos(faker.number().numberBetween(100, 500));
                promo.setUsosActuales(faker.number().numberBetween(0, 50));
                promo.setEstado(true);

                promo.setProductoId(faker.options().option(null, 1L, 5L));
                promo.setCategoriaId(faker.options().option(null, 2L, 4L));

                repository.save(promo);
            }
            System.out.println("--- 5 Promociones generadas exitosamente ---");
        };
    }
}