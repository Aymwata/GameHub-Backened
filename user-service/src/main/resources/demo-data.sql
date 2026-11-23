-- Los IDs coinciden con usuario_id en la base de autenticación.
INSERT INTO usuarios (id, nombre, email, telefono, rol, estado, created_at, updated_at)
SELECT demo.id, demo.nombre, demo.email, '900000000', demo.rol, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM (VALUES
    (10001, 'Administrador de prueba', 'admin@gamehub.cl', 'ADMIN'),
    (10002, 'Cliente de prueba', 'cliente@gamehub.cl', 'CLIENTE')
) AS demo(id, nombre, email, rol)
WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE LOWER(email) = demo.email);
