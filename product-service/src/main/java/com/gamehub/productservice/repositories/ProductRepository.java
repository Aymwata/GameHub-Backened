package com.gamehub.productservice.repositories;

import com.gamehub.productservice.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Al igual que con Category, Spring Boot nos regala el CRUD básico
}
