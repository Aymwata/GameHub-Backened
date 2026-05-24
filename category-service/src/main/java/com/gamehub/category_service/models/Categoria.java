package com.gamehub.category_service.models; // Ajusta 'com.gamehub' a tu paquete real

import com.gamehub.authservice.models.Audit;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity // Le dice a Spring que esta clase es una tabla en la base de datos
@Table(name = "categorias") // Es buena práctica poner el nombre de la tabla en plural
@Data // Magia de Lombok: nos ahorra escribir getters, setters y toString
@NoArgsConstructor // Lombok: crea un constructor vacío (obligatorio para JPA)
@AllArgsConstructor // Lombok: crea un constructor con todos los campos
@EqualsAndHashCode(callSuper = true)
public class Categoria extends Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Autoincrementable en la BD
    private Long id;

    @Column(unique = true, nullable = false) // Regla de negocio: "Nombre de categoría único"
    private String nombre;

    @Column(name = "descripcion", nullable = true, length = 255)
    private String descripcion;

    // Usamos Boolean para "Activo/Inactivo", cumpliendo con la regla de no eliminar físicamente
    @Column(nullable = false)
    private Boolean estado = true;

}
