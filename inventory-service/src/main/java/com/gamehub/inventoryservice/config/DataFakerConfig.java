package com.gamehub.inventoryservice.config;

import com.gamehub.inventoryservice.models.Inventory;
import com.gamehub.inventoryservice.repositories.InventoryRepository;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataFakerConfig {

    @Bean
    CommandLineRunner initInventoryDatabase(InventoryRepository repository) {
        return args -> {
            Faker faker = new Faker();

            System.out.println("--- Generando datos falsos de Inventario (DataFaker) ---");

            for (int i = 0; i < 5; i++) {
                Inventory invFalso = new Inventory();

                invFalso.setProductId(faker.number().numberBetween(1L, 50L));
                invFalso.setStockAvailable(faker.number().numberBetween(50, 200));
                invFalso.setStockReserved(faker.number().numberBetween(0, 15));
                invFalso.setLocation("Bodega " + faker.options().option("Norte", "Sur", "Central") + " - Estante " + faker.number().numberBetween(1, 10));
                invFalso.setMinStock(5);


                repository.save(invFalso);
            }

            System.out.println("--- 5 Registros de inventario generados exitosamente ---");
        };
    }
}