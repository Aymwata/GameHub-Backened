-- Los IDs coinciden con las cargas de categorías e inventario.
INSERT INTO productos (id, nombre, marca, modelo, descripcion, imagen, especificaciones, precio, categoria_id, estado, created_at, updated_at)
SELECT demo.id, demo.nombre, demo.marca, demo.modelo, demo.descripcion, demo.imagen, demo.especificaciones,
       demo.precio, demo.categoria_id, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM (VALUES
    (10001, 'Monitor Gamer Samsung 27", FHD, 180Hz, VA, 1ms, FreeSync, Odyssey G3 LS27DG300ELXZS', 'Samsung', 'LS27DG300ELXZS',
     'Monitor gamer Samsung Odyssey G3 de 27 pulgadas con resolución Full HD. Diseñado para eSports con una tasa de refresco ultra rápida de 180Hz y un tiempo de respuesta de 1ms MPRT que elimina el desenfoque de movimiento. Su panel VA con AMD FreeSync garantiza una experiencia fluida sin interrupciones ni tearing.',
     '/assets/images/monitorSamsung.jpg', '{"pantalla":"27 pulgadas Full HD (1920 x 1080)","tasaRefresco":"180 Hz","tiempoRespuesta":"1 ms (MPRT)","panel":"VA de alto contraste","sincronizacion":"AMD FreeSync","conectividad":"1x DisplayPort 1.2, 1x HDMI 2.0"}' FORMAT JSON, 180000, 10003),
    (10002, 'Notebook Gamer LOQ Gen 10 Intel Core i7-13645HX NVIDIA GeForce RTX 5050 15.6" 144Hz 16GB RAM 512GB Windows 11 Home', 'Lenovo', 'LOQ Gen 10',
     'Notebook de alto rendimiento Lenovo LOQ equipado con procesador Intel Core i7 de 13ª generación y la tarjeta gráfica dedicada NVIDIA GeForce RTX 5050. Cuenta con pantalla FHD de 144Hz para partidas ultra fluidas y un sistema de refrigeración térmica optimizado con doble ventilador.',
     '/assets/images/notebookLenovo.jpg', '{"procesador":"Intel Core i7-13645HX","tarjetaGrafica":"NVIDIA GeForce RTX 5050 6GB GDDR6","memoriaRam":"16 GB DDR5 (Ampliable)","almacenamiento":"512 GB SSD NVMe M.2 PCIe","pantalla":"15.6\" FHD (1920 x 1080) IPS 144Hz","sistemaOperativo":"Windows 11 Home"}' FORMAT JSON, 850000, 10001),
    (10003, 'Tarjeta de Video MSI Nvidia GeForce RTX 5060 8G VENTUS 2X OC WHITE', 'MSI', 'RTX 5060 8G VENTUS 2X OC WHITE',
     'Tarjeta gráfica MSI GeForce RTX 5060 con diseño completamente blanco de la línea VENTUS 2X. Ofrece Overclocking de fábrica, dos ventiladores TORX Fan 4.0 para óptima disipación y soporte completo para Ray Tracing de última generación y escalado AI con DLSS.',
     '/assets/images/tarjetaNvidia.jpg', '{"memoria":"8 GB GDDR6","busMemoria":"128-bit","frecuenciaBoost":"Modo OC impulsado por software","refrigeracion":"Doble ventilador Ventus 2X TORX 4.0","salidasVideo":"3x DisplayPort 1.4a, 1x HDMI 2.1a","edicion":"White Edition (Color blanco)"}' FORMAT JSON, 320000, 10002),
    (10004, 'Desktop Gamer Esgaming Ryzen 5 5500 32GB DDR4 RGB 500GB SSD RTX 5050', 'Esgaming', 'Ryzen 5 5500 RTX 5050',
     'Equipo de escritorio ensamblado especialmente para Gaming exigente, Streaming y creación de contenido. Equipado con procesador AMD Ryzen 5 5500 de 6 núcleos, 32GB de memoria RAM multitarea y gráficos dedicados NVIDIA RTX 5050 sobre un gabinete con iluminación RGB personalizable.',
     '/assets/images/desktopEsgaming.jpg', '{"procesador":"AMD Ryzen 5 5500 (6 Núcleos / 12 Hilos)","tarjetaGrafica":"NVIDIA GeForce RTX 5050","memoriaRam":"32 GB DDR4 RGB","almacenamiento":"500 GB SSD M.2 NVMe","fuentePoder":"600W certificación 80 Plus","gabinete":"Esgaming Cristal Templado con fanes RGB"}' FORMAT JSON, 1200000, 10001),
    (10005, 'Monitor Gamer Samsung Curvo 49” Odyssey G9 G91F QHD 144Hz', 'Samsung', 'Odyssey G9 G91F',
     'Monitor Super Ultra-Wide curvo de 49 pulgadas Samsung Odyssey G9 con relación de aspecto 32:9 (equivalente a dos pantallas QHD juntas). Su curvatura 1000R envuelve tu campo de visión, mientras que su tasa de refresco de 144Hz e iluminación trasera CoreSync brindan una inmersión completa.',
     '/assets/images/monitorSamsungCurvo.jpg', '{"pantalla":"49 pulgadas Dual QHD (5120 x 1440)","curvatura":"1000R ultracurvo","tasaRefresco":"144 Hz","panel":"VA QLED","brilloHDR":"Soporte HDR600","conectividad":"2x DisplayPort 1.4, 1x HDMI 2.0, USB Hub"}' FORMAT JSON, 849990, 10003)
) AS demo(id, nombre, marca, modelo, descripcion, imagen, especificaciones, precio, categoria_id)
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE id = demo.id);
