##Descripción del Proyecto
GameHub Store es un sistema de backend para una tienda gamer online dedicada a la venta de productos como 
notebooks, tarjetas gráficas, procesadores, periféricos, consolas, monitores y accesorios. El proyecto resuelve 
el problema de coordinar una operación de venta completa mediante una solución distribuida, evitando depender 
de un sistema monolítico. Para lograrlo, el sistema gestiona áreas clave como el catálogo, categorías, inventario, 
órdenes, pagos, despachos, promociones, reseñas, garantías y notificaciones internas.


##Integrantes del Equipo
* Martin Naranjo
* Maximiliano Isamit
* Ariel Rojo


##Arquitectura
**Base de Datos:** Cada microservicio tiene su propia base de datos, basado en H2.
**Patrón de Diseño:** Los microservicios están organizados utilizando el patrón CSR.
**Comunicación Interna:** Integración en flujos principales mediante OpenFeign.



##Funcionalidades Implementadas (Microservicios)
El ecosistema está compuesto por los siguientes microservicios, cada uno con su propio CRUD completo 
(crear, listar, buscar, actualizar y eliminar/desactivar) y responsabilidades únicas:

auth-service:  Gestiona el acceso seguro a la tienda, permitiendo el inicio de sesión y la administración de cuentas de acceso.

user-service:  Administra los perfiles de clientes, operadores y administradores, guardando sus datos de contacto y direcciones de despacho.

product-service:  Gestiona el catálogo de productos, manteniendo todos sus datos comerciales y técnicos básicos.

category-service:  Administra las categorías comerciales para clasificar los productos, como GPU, CPU, monitores, consolas, entre otros.

inventory-service:  Controla de manera crítica el stock disponible, reservado y vendido de cada producto, registrando cada movimiento de inventario.

order-service:  Gestiona las órdenes de compra de los clientes, coordinando de forma centralizada la validación de stock, la aplicación de 
                promociones, el pago y el despacho.
                
payment-service:  Procesa y simula los pagos de las órdenes, registrando el método, monto, estado y la fecha de transacción.
                
shipping-service:  Gestiona el despacho exclusivo de las órdenes pagadas, manteniendo los datos del transportista y el número de seguimiento.

promotion-service:  Administra cupones, descuentos y campañas comerciales para ser aplicados en las órdenes.

review-service:  Permite a los usuarios calificar y comentar aquellos productos que efectivamente han comprado.



##Pruebas de ejecucion:

USER-SERVICE:
POST ------- http://localhost:8080/api/users

{
 "nombre": "Ariel Rojo",
 "email":
"ariel.rojo@gamehub.cl",
 "telefono":
"911111111",
 "rol": "ADMIN"
}

-------

{
 "nombre": "Martin Naranjo",
 "email":
"martin.naranjo@gamehub.cl",
 "telefono":
"922222222",
 "rol": "OPERADOR"
}

-------

{
 "nombre": "Maximiliano Isamit",
 "email":
"maximiliano.isamit@gamehub.cl",
 "telefono":
"933333333",
 "rol": "CLIENTE"
}
 
 
Dirección del cliente (Es importante añadirlo, debido a que si el cliente no tiene una direccion, no se podra enviar el producto):
POST - http://localhost:8080/api/directions

{
 "usuarioId":
1,
 "calle": "Avenida del Mar",
 "numero": "1045",
 "comuna": "Puchuncavi",
 "ciudad": "Valparaiso"
}



AUTH-SERVICE:

POST ------- http://localhost:8082/api/auth/cuentas
{
 "email": "ariel.rojo@gamehub.cl",
 "password": "Ariel",
 "rol": "ADMIN"
}
 
{
 "email": "martin.naranjo@gamehub.cl",
 "password": "Martin",
 "rol": "OPERADOR"
}
 
{
 "email": "maximiliano.isamit@gamehub.cl",
 "password":
"Maximiliano",
 "rol": "CLIENTE"
}
 
 
 
CATEGORY-SERVICE:
POST ------- http://localhost:8081/api/categories

{
 "nombre": "Aventura",
 "descripcion": "Juegos de exploracion mundo abierto y
rol"
}
 
 
 
PRODUCT-SERVICE:
POST ------- http://localhost:8083/api/products

{
 "nombre": "The Legend of Zelda: Tears of the
Kingdom",
 "marca":
"Nintendo",
 "modelo": "Switch",
 "descripcion": "Secuela directa de Breath of the
Wild",
 "precio": 69.99,
 "categoriaId": 1
}
 
 
INVENTORY-SERVICE:
POST ------- http://localhost:8085/api/inventories

{
 "productId": 1,
 "quantity": 150,
 "location": "Bodega Central"
}
 
 
PROMOTION-SERVICE:
POST ------- http://localhost:8084/api/promotions
 
{
 "codigo": "GAMER2026",
 "tipo": "PORCENTAJE",
 "valor": 15.0,
 "fechaInicio": "2026-05-23T00:00:00",
 "fechaFin": "2026-12-31T23:59:59",
 "montoMinimo": 50.0,
 "usosMaximos": 100,
 "productoId": 1,
 "categoriaId": 1
}
 
 
  
ORDER-SERVICE:
POST ------- http://localhost:8086/api/orders

{
  "usuarioId": 1,
  "codigoPromocion":
"GAMER2026",
  "detalles": [
    {
      "productoId": 1,
      "cantidad": 2
   }
 ]
}


PAYMENT-SERVICE:
POST ------- http://localhost:8089/api/payments
 
dependiendo de cuando fueron ejecutados los JSON del promotion, podría haber vencido el cupon
o el numero puede ser un decimal muy extenso, verificar la orden de compra en el order-service con un GET. 
el coste deberia ser (reemplazar por el valor del json):
118.98299999999999.
 
{
  "ordenId": 1,
  "monto": 118.98,
  "metodo":
"TARJETA_CREDITO"
}


 
SHIPPING-SERVICE:
POST ------- http://localhost:8087/api/shippings
 
{
 "orderId": 1,
 "userId": 1,
 "carrier": "Chilexpress"
}

 
GET – para ver seguimiento 
http://localhost:8088/api/shippings/1?estado=EN_TRANSITO&trackingNumber=CHX-99887766



REVIEW-SERVICE:
POST ------- http://localhost:8088/api/resenas
 
{
  "usuarioId": 1,
  "productoId": 1,
  "ordenId": 1,
  "puntuacion": 5,
  "comentario": "¡GOTY! El proceso de compra fue rapidísimo y el juego llegó en perfectas condiciones."
}



