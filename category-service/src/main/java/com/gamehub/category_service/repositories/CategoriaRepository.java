package com.gamehub.category_service.repositories; // Ajusta el paquete

import com.gamehub.category_service.models.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

}

