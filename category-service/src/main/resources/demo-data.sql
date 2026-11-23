-- IDs reservados para las categorías del catálogo de prueba.
INSERT INTO categorias (id, nombre, descripcion, estado, created_at, updated_at)
SELECT demo.id, demo.nombre, demo.descripcion, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM (VALUES
    (10001, 'Computadores', 'Notebooks y equipos de escritorio'),
    (10002, 'Componentes y piezas', 'Tarjetas gráficas y componentes para tu equipo'),
    (10003, 'Periféricos', 'Monitores y accesorios gamer')
) AS demo(id, nombre, descripcion)
WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE id = demo.id);
