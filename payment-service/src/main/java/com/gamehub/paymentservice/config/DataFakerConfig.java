package com.gamehub.paymentservice.config;

import com.gamehub.paymentservice.models.Pago;
import com.gamehub.paymentservice.repositories.PagoRepository;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataFakerConfig {

    @Bean
    CommandLineRunner initDatabase(PagoRepository repository) {
        return args -> {
            Faker faker = new Faker();

            System.out.println("--- Generando datos falsos de Pagos (DataFaker) ---");

            for (int i = 0; i < 5; i++) {
                Pago pagoFalso = new Pago();


                pagoFalso.setOrdenId(faker.number().numberBetween(1L, 100L));
                pagoFalso.setMonto(faker.number().randomDouble(2, 10, 500));
                pagoFalso.setMetodo(faker.options().option("CREDIT_CARD", "PAYPAL", "DEBIT"));
                pagoFalso.setEstado(faker.options().option("COMPLETED", "PENDING", "FAILED"));
                pagoFalso.setCodigoTransaccion(faker.bothify("TXN-########")); // Genera códigos como TXN-12345678
                pagoFalso.setUsuarioId(faker.number().numberBetween(1L, 50L));



                repository.save(pagoFalso);
            }

            System.out.println("--- 5 Pagos generados exitosamente ---");
        };
    }
}