package com.gamehub.category_service.Models; // Ajusta 'com.gamehub' a tu paquete real

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity // Le dice a Spring que esta clase es una tabla en la base de datos
@Table(name = "categorias") // Es buena práctica poner el nombre de la tabla en plural
@Data // Magia de Lombok: nos ahorra escribir getters, setters y toString
@NoArgsConstructor // Lombok: crea un constructor vacío (obligatorio para JPA)
@AllArgsConstructor // Lombok: crea un constructor con todos los campos
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Autoincrementable en la BD
    private Long id;

    // JSR 380: Validaciones directas en la entidad para proteger la BD
    @NotBlank(message = "El nombre de la categoría es obligatorio")
    @Size(min = 3, max = 50, message = "El nombre debe tener entre 3 y 50 caracteres")
    @Column(unique = true, nullable = false) // Regla de negocio: "Nombre de categoría único"
    private String nombre;

    @Size(max = 255, message = "La descripción no puede superar los 255 caracteres")
    private String descripcion;

    // Usamos Boolean para "Activo/Inactivo", cumpliendo con la regla de no eliminar físicamente
    @Column(nullable = false)
    private Boolean estado = true;
}
