# GameHub - Backend Microservices Architecture

![Spring Boot](https://img.shields.io/badge/Spring_Boot-F2F4F9?style=for-the-badge&logo=spring-boot)
![Spring Cloud](https://img.shields.io/badge/Spring_Cloud-6DB33F?style=for-the-badge&logo=spring)
![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=JSON%20web%20tokens&logoColor=white)

Bienvenido al repositorio oficial del backend de **GameHub**, una plataforma e-commerce moderna construida bajo una **Arquitectura de Microservicios**. Este proyecto fue desarrollado como parte de la Evaluación 3, enfocándose en la escalabilidad, seguridad y pruebas automatizadas.

---

## Arquitectura del Sistema

El ecosistema está compuesto por un Directorio de Descubrimiento, un API Gateway centralizado y 10 microservicios de negocio independientes.

### Componentes Core (Infraestructura)
* **Eureka Server (`eureka-server`):** Directorio telefónico dinámico. Permite que los microservicios se registren y se descubran entre sí por nombre, eliminando la dependencia de IPs fijas y permitiendo la escalabilidad horizontal.
* **API Gateway (`gateway-service`):** Puerta de entrada única (Puerto `8099`) para clientes externos. Implementa enrutamiento (Load Balancing) e intercepta las peticiones para validar la seguridad (**Tokens JWT**) antes de delegarlas a los servicios internos.

### Microservicios de Negocio
1. **Auth Service:** Emisión de tokens JWT y gestión de credenciales.
2. **Category Service:** Gestión del árbol de categorías de la tienda.
3. **Product Service:** Catálogo central de productos.
4. **Shipping Service:** Logística, creación de guías de despacho y tracking.
5. **Inventory Service:** Control de stock y movimientos de inventario.
6. **Order Service:** Orquestador de carritos y órdenes de compra.
7. **Payment Service:** Procesamiento de transacciones.
8. **Promotion Service:** Gestión de descuentos y campañas.
9. **Review Service:** Calificaciones y reseñas de usuarios.
10. **User Service:** Perfiles de clientes y libreta de direcciones.

---

## Tecnologías Utilizadas

* **Framework Principal:** Spring Boot 3 / Java
* **Red y Enrutamiento:** Spring Cloud Netflix Eureka, Spring Cloud Gateway (WebMVC)
* **Comunicación Interna:** OpenFeign Client
* **Seguridad:** Spring Security, OAuth2 Resource Server (JWT Stateless)
* **Base de Datos:** H2 Database (In-Memory / File-based por microservicio)
* **Documentación API:** SpringDoc OpenAPI (Swagger UI)
* **Testing:** JUnit 5, Mockito

---
##Rutas a usar para probar swagger:

http://localhost:8080/doc/swagger-ui/index.html       -->USER-SERVICE
http://localhost:8081/doc/swagger-ui/index.html       -->CATEGORY-SERVICE
http://localhost:8082/doc/swagger-ui/index.html       -->AUTH-SERVICE
http://localhost:8083/doc/swagger-ui/index.html       -->PRODUCT-SERVICE
http://localhost:8084/doc/swagger-ui/index.html       -->PROMOTION-SERVICE
http://localhost:8085/doc/swagger-ui/index.html       -->INVENTORY-SERVICE
http://localhost:8086/doc/swagger-ui/index.html       -->ORDER-SERVICE
http://localhost:8087/doc/swagger-ui/index.html       -->SHIPPING-SERVICE
http://localhost:8088/doc/swagger-ui/index.html       -->REVIEW-SERVICE
http://localhost:8089/doc/swagger-ui/index.html       -->PAYMENT-SERVICE


##Ruta de eureka:

http://localhost:8761

##Ruta de api gateway:

http://localhost:8099      