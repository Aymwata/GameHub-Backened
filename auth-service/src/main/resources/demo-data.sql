-- Hashes PBKDF2 de las contraseñas ficticias indicadas en el README.
INSERT INTO cuentas_acceso (email, password, rol, estado, usuario_id, created_at, updated_at)
SELECT demo.email, demo.password_hash, demo.rol, TRUE, demo.usuario_id, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM (VALUES
    ('admin@gamehub.cl', '{pbkdf2@SpringSecurity_v5_8}54e79adbb8166e748c028fcc7cae22906dd59a401b655d3c277d33618220566299ee685a2fae8eff18bff8dc31b18d03', 'ADMIN', 10001),
    ('cliente@gamehub.cl', '{pbkdf2@SpringSecurity_v5_8}7b45b8a13b8805a2fb34985990424a6de28f1f781985f2c502e6eb6c4fb9f4e62172100612f69b7a7ba361ced1666fca', 'CLIENTE', 10002)
) AS demo(email, password_hash, rol, usuario_id)
WHERE NOT EXISTS (SELECT 1 FROM cuentas_acceso WHERE LOWER(email) = demo.email);
