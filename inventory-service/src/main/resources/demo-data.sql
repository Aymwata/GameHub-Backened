INSERT INTO inventories (product_id, stock_available, stock_reserved, location, min_stock, created_at, updated_at)
SELECT demo.product_id, demo.stock, 0, 'Bodega GameHub', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM (VALUES
    (10001, 15),
    (10002, 5),
    (10003, 0),
    (10004, 2),
    (10005, 10)
) AS demo(product_id, stock)
WHERE NOT EXISTS (SELECT 1 FROM inventories WHERE product_id = demo.product_id);
