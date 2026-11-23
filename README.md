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

* **Framework Principal:** Spring Boot 4 / Java
* **Red y Enrutamiento:** Spring Cloud Netflix Eureka, Spring Cloud Gateway (WebMVC)
* **Comunicación Interna:** OpenFeign Client
* **Seguridad:** Spring Security, OAuth2 Resource Server (JWT Stateless)
* **Base de Datos:** H2 Database (In-Memory / File-based por microservicio)
* **Documentación API:** SpringDoc OpenAPI (Swagger UI)
* **Testing:** JUnit 5, Mockito

## Arranque local para la Evaluación 3

Necesitas IntelliJ IDEA con soporte de Spring Boot y un JDK 25. En el primer arranque, Maven descarga las dependencias de cada servicio.

1. Clona la rama `martin` y abre la carpeta raíz `GameHub-Backened` en IntelliJ. Importa el `pom.xml` de la raíz como proyecto Maven y selecciona JDK 25 en **File → Project Structure → Project**.
2. En el menú junto al botón **Run** (triángulo verde), ejecuta **GameHub Demo - Eureka**. Espera a que abra el [panel de Eureka](http://127.0.0.1:8761/).
3. Ejecuta **GameHub Demo - Microservicios**. Este grupo inicia los diez servicios de negocio. Espera a que aparezcan registrados en Eureka.
4. Ejecuta **GameHub Demo - Gateway** y comprueba que abre el [catálogo del backend](http://127.0.0.1:8099/api/products). Si acabas de iniciar el Gateway, dale unos segundos para que lea el registro de Eureka.

Para apagar el backend, abre **View → Tool Windows → Services** y usa el botón rojo **Stop** de los servicios que estén activos. Deja Eureka para el final.

### Rutas locales

| Para qué | Dirección |
| --- | --- |
| Ver qué servicios están activos | [Eureka](http://127.0.0.1:8761/) |
| Probar la API del backend | [Swagger del Gateway](http://127.0.0.1:8099/docs/swagger-ui.html) |
| Consultar los productos | [Catálogo](http://127.0.0.1:8099/api/products) |
| Abrir la tienda con el frontend en ejecución | [GameHub](http://127.0.0.1:5173/) |
| Entrar como cliente | [Login de cliente](http://127.0.0.1:5173/login) |
| Entrar como administrador | [Login de administrador](http://127.0.0.1:5173/admin/login) |

### Datos de prueba

| Rol | Correo | Contraseña inicial |
| --- | --- | --- |
| Administrador | `admin@gamehub.cl` | `admin1234` |
| Cliente | `cliente@gamehub.cl` | `cliente1234` |

Las configuraciones compartidas están en `.run`. Crean sus propias bases H2 en `.local/demo-data` y cargan las cuentas, categorías, productos e inventario desde los archivos `src/main/resources/demo-data.sql` de cada servicio. El [SQL de Product](product-service/src/main/resources/demo-data.sql) contiene las fichas iniciales. Los datos se conservan al reiniciar y los archivos de H2 quedan fuera de Git. Las bases de arranques anteriores que usaban `.local/data` se pueden seguir usando desde sus configuraciones locales de IntelliJ.

Para agregar productos o ingresar stock, usa la [vista de administración](http://127.0.0.1:5173/admin). Para cambiar una contraseña de prueba desde la API, inicia sesión como administrador, consulta `GET /api/auth/cuentas` y usa `PUT /api/auth/cuentas/{id}` con el correo, el rol y la nueva contraseña.

Las configuraciones de la evaluación escuchan en `127.0.0.1` y llevan una clave JWT pública de prueba. En un despliegue, usa un `JWT_SECRET` privado de al menos 32 bytes, compartido por Auth, Order y Gateway, y configura HTTPS.

### Contrato para las pantallas de cliente y administrador

Todas las rutas de esta tabla pasan por el Gateway en el puerto `8099`. Las dos pantallas usan el mismo endpoint con el valor de `perfil` correspondiente. El servidor valida ese valor contra el rol que tiene asignado la cuenta.

| Solicitud | Datos | Resultado |
| --- | --- | --- |
| `POST /api/auth/login` | JSON con `email`, `password` y `perfil` (`CLIENTE` o `ADMIN`) | `200` con `accessToken`, `expiresIn` en segundos y `usuario` con `id`, `nombre`, `email` y `rol`. `401` si las credenciales son inválidas o la cuenta está inactiva. `403` si la cuenta pertenece a la otra vista. |
| `POST /api/auth/registro` | JSON con `nombre`, `email`, `telefono` y `password` (mínimo 8 caracteres) | `201` con el usuario creado como `CLIENTE`. Para iniciar sesión se usa el endpoint de login. |
| `POST /api/auth/refresh` | Cookie de sesión y cabecera `X-GameHub-Session: 1` | `200` con el mismo formato del login, un JWT nuevo y una cookie nueva. `401` si la sesión venció o ya se usó esa cookie para renovar. |
| `POST /api/auth/logout` | Cookie de sesión y cabecera `X-GameHub-Session: 1` | `204`. Revoca la renovación y borra la cookie del navegador. El JWT de acceso previo conserva su vencimiento original. |

El login y la renovación envían la cookie `gamehub_refresh` con `HttpOnly` y `SameSite=Strict`. El JWT dura 10 minutos y se envía en `Authorization: Bearer <accessToken>` para las rutas protegidas. La sesión renovable vence a los 7 días desde el login. React guarda el JWT de acceso en memoria y llama a `refresh` al recargar. Vite reenvía `/api` al Gateway durante el desarrollo. Login, registro, renovación y cierre se llaman sin cabecera `Authorization`.

El catálogo se puede leer sin iniciar sesión. El cliente puede crear y consultar sus órdenes en `/api/orders/me` y consultar un detalle en `/api/orders/me/{id}`. Para crear una orden envía `codigoPromocion` si corresponde y `detalles` con pares de `productoId` y `cantidad`. El checkout también envía `datosCheckout` con el contacto, la dirección y el método de pago preferido. El backend obtiene el ID del usuario desde el JWT y guarda el pedido en estado pendiente de pago. Los cambios en categorías y la gestión de cuentas requieren un JWT de administrador.

Expón únicamente el Gateway y conserva los puertos de los servicios en la red privada. Usa HTTPS para la cookie segura fuera del arranque local. Para crear el primer administrador en otro entorno, prepara su perfil en usuarios y configura `GAMEHUB_AUTH_BOOTSTRAP_ADMIN_EMAIL` y `GAMEHUB_AUTH_BOOTSTRAP_ADMIN_PASSWORD` al iniciar autenticación.

Con Node.js 24 y el backend en ejecución, abre una terminal en la carpeta que contiene ambos repositorios y ejecuta:

```powershell
Set-Location .\GameHub_Frontend\frontend
npm.cmd ci
npm.cmd run dev -- --host 127.0.0.1
```

Abre la dirección que imprime Vite. Para detener React, presiona `Ctrl+C` en su terminal.
