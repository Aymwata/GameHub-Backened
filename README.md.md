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

## Arranque local para la Evaluación 3

Ejecuta este comando en PowerShell desde la carpeta que contiene ambos repositorios.

```powershell
powershell.exe -NoProfile -ExecutionPolicy Bypass -File .\GameHub-Backened\scripts\local.ps1
```

Se requiere un JDK 25. Si está instalado en otra carpeta o `JAVA_HOME` apunta a otro JDK, ajusta `$env:JAVA_HOME` en la terminal antes de ejecutar el comando. La primera compilación puede requerir acceso a Maven Central. El script usa los Maven Wrappers de los módulos.

El arranque inicia Eureka, Gateway, usuarios, categorías, autenticación, productos, inventario y órdenes. Agrega `-AllServices` para incluir promociones, despacho, reseñas y pagos. El script usa solo la interfaz local y guarda sus bases H2, logs y credenciales ficticias en `GameHub-Backened/.local/`. Desactiva la generación aleatoria de inventario y pagos para esta ejecución. Al reiniciar conserva esas bases y carga únicamente los registros de prueba que falten.

- Eureka queda en `http://localhost:8761/`.
- Swagger del Gateway queda en `http://localhost:8099/docs/swagger-ui.html`. El selector de Swagger incluye también los servicios opcionales cuando están iniciados.
- El catálogo de prueba se puede consultar en `http://localhost:8083/api/products`.
- Las dos cuentas de demo se guardan en `GameHub-Backened/.local/demo-credentials.json`. El arranque verifica que ambas puedan obtener un JWT.

### Contrato para las pantallas de cliente y administrador

Todas las rutas de esta tabla pasan por el Gateway en el puerto `8099`. Las dos pantallas usan el mismo endpoint con el valor de `perfil` correspondiente. El servidor valida ese valor contra el rol que tiene asignado la cuenta.

| Solicitud | Datos | Resultado |
| --- | --- | --- |
| `POST /api/auth/login` | JSON con `email`, `password` y `perfil` (`CLIENTE` o `ADMIN`) | `200` con `accessToken`, `expiresIn` en segundos y `usuario` con `id`, `nombre`, `email` y `rol`. `401` si las credenciales son inválidas o la cuenta está inactiva. `403` si la cuenta pertenece a la otra vista. |
| `POST /api/auth/registro` | JSON con `nombre`, `email`, `telefono` y `password` (mínimo 8 caracteres) | `201` con el usuario creado como `CLIENTE`. Para iniciar sesión se usa el endpoint de login. |
| `POST /api/auth/refresh` | Cookie de sesión y cabecera `X-GameHub-Session: 1` | `200` con el mismo formato del login, un JWT nuevo y una cookie nueva. `401` si la sesión venció o ya se usó esa cookie para renovar. |
| `POST /api/auth/logout` | Cookie de sesión y cabecera `X-GameHub-Session: 1` | `204`. Revoca la renovación y borra la cookie del navegador. El JWT de acceso previo conserva su vencimiento original. |

El login y la renovación envían la cookie `gamehub_refresh` con `HttpOnly` y `SameSite=Strict`. El JWT dura 10 minutos y se envía en `Authorization: Bearer <accessToken>` para las rutas protegidas. La sesión renovable vence a los 7 días desde el login. El cliente puede guardar el JWT de acceso en memoria y llamar a `refresh` al recargar. Para la futura integración en React se propone servir `/api` en el mismo origen mediante un proxy de Vite en desarrollo. Login, registro, renovación y cierre se llaman sin cabecera `Authorization`.

El catálogo se puede leer sin iniciar sesión. El cliente puede crear y consultar sus órdenes en `/api/orders/me` y consultar un detalle en `/api/orders/me/{id}`. Para crear una orden envía `codigoPromocion` si corresponde y `detalles` con pares de `productoId` y `cantidad`. El backend obtiene el ID del usuario desde el JWT. Los cambios en categorías y la gestión de cuentas requieren un JWT de administrador.

El script fija una clave JWT local compartida entre los servicios. Para ejecutarlos por separado, configura la misma variable `JWT_SECRET` de al menos 32 bytes en Gateway, autenticación y órdenes. Expón únicamente el Gateway y conserva los puertos de los servicios en la red privada. Usa HTTPS para la cookie segura fuera del arranque local. Para crear el primer administrador en otro entorno, prepara su perfil en usuarios y configura `GAMEHUB_AUTH_BOOTSTRAP_ADMIN_EMAIL` y `GAMEHUB_AUTH_BOOTSTRAP_ADMIN_PASSWORD` al iniciar autenticación.

En otra terminal, desde la carpeta que contiene ambos repositorios, inicia React.

```powershell
Set-Location .\GameHub_Frontend\frontend
npm.cmd ci
npm.cmd run dev -- --host 127.0.0.1
```

Abre la dirección que imprime Vite. Para detener React, presiona `Ctrl+C` en su terminal. Para detener el backend, ejecuta este comando desde la carpeta que contiene ambos repositorios.

```powershell
powershell.exe -NoProfile -ExecutionPolicy Bypass -File .\GameHub-Backened\scripts\local.ps1 -Action Stop
```

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
