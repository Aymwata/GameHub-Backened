package com.gamehub.category_service.repositories; // Ajusta el paquete

import com.gamehub.category_service.models.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    // Al heredar de JpaRepository, Spring Boot nos regala el CRUD completo:
    // save(), findAll(), findById(), deleteById()
    // No tienes que escribir NADA de código SQL.

}

