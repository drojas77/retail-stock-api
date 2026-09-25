-- NECESARIO PARA gen_random_uuid()
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- =======================================
--  MUESTRA DE DATOS
-- =======================================

-- =======================================
--  MUESTRA DE DATOS
-- =======================================


-- 1. MARCAS
INSERT INTO marcas (id, nombre) VALUES
(1, 'Nike'),
(2, 'Adidas'),
(3, 'New Balance'),
(4, 'Puma'),
(5, 'Asics')
ON CONFLICT (id) DO NOTHING;

-- 2. TALLAS
INSERT INTO tallas (id, numero) VALUES
(1, '39'),
(2, '40'),
(3, '41'),
(4, '42'),
(5, '43'),
(6, '44'),
(7, '45')
ON CONFLICT (id) DO NOTHING;

-- 3. PRODUCTOS (100% CALZADO)
INSERT INTO productos (id, nombre, descripcion, precio_base, categoria, color, temporada, marca_id, genero) VALUES
(1, 'Nike Air Max 90', 'Icono streetwear con camara de aire', 129.99, 'DEPORTIVO', 'BLANCO', 'VERANO 2026', 1, 'UNISEX'),
(2, 'Adidas Gazelle Low', 'Clasico retro en ante', 109.99, 'CASUAL', 'NEGRO', 'INVIERNO 2026', 2, 'UNISEX'),
(3, 'New Balance 550', 'Basket retro 90s', 119.90, 'BASKET', 'BLANCO/VERDE', 'VERANO 2026', 3, 'HOMBRE'),
(4, 'Puma Suede XL', 'Tendencia 2026 suede ancho', 99.99, 'CASUAL', 'AZUL', 'INVIERNO 2026', 4, 'MUJER'),
(5, 'Asics Gel-Kayano 14', 'Running stability para larga distancia', 139.99, 'RUNNING', 'GRIS', 'INVIERNO 2026', 5, 'HOMBRE')
ON CONFLICT (id) DO NOTHING;

-- 4. VARIANTES (PRODUCTO + TALLA + SKU)
INSERT INTO variantes_producto (id, producto_id, talla_id, sku_modelo, precio_especifico) VALUES
(1, 1, 2, 'NIKE-NIKE-BLAN-40', 129.99),
(2, 1, 4, 'NIKE-NIKE-BLAN-42', 129.99),
(3, 1, 5, 'NIKE-NIKE-BLAN-43', 134.99),
(4, 2, 3, 'ADID-GAZE-NEGR-41', 109.99),
(5, 2, 4, 'ADID-GAZE-NEGR-42', 109.99),
(6, 3, 4, 'NEWB-NEWB-BLAN-42', 119.90),
(7, 3, 5, 'NEWB-NEWB-BLAN-43', 119.90),
(8, 4, 1, 'PUMA-SUED-AZUL-39', 99.99),
(9, 5, 6, 'ASIC-ASIC-GRIS-44', 139.99)
ON CONFLICT (id) DO NOTHING;

-- 5. CAJAS STOCK (QR REALES PARA ESCANEAR)
INSERT INTO cajas_stock (variante_id, qr_codigo_unico, estado, ubicacion_almacen, created_at) VALUES
(1, 'QR-NIKE-NIKE-BLAN-40-' || upper(substr(gen_random_uuid()::text, 1, 13)), 'DISPONIBLE', 'A1-01-01',NOW()),
(1, 'QR-NIKE-NIKE-BLAN-40-' || upper(substr(gen_random_uuid()::text, 1, 13)), 'DISPONIBLE', 'A1-01-02',NOW()),
(2, 'QR-NIKE-NIKE-BLAN-42-' || upper(substr(gen_random_uuid()::text, 1, 13)), 'DISPONIBLE', 'A1-02-05',NOW()),
(2, 'QR-NIKE-NIKE-BLAN-42-' || upper(substr(gen_random_uuid()::text, 1, 13)), 'DISPONIBLE', 'A1-02-06',NOW()),
(4, 'QR-ADID-GAZE-NEGR-41-' || upper(substr(gen_random_uuid()::text, 1, 13)), 'DISPONIBLE', 'B1-03-10',NOW()),
(6, 'QR-NEWB-NEWB-BLAN-42-' || upper(substr(gen_random_uuid()::text, 1, 13)), 'DISPONIBLE', 'B2-01-01',NOW()),
(6, 'QR-NEWB-NEWB-BLAN-42-' || upper(substr(gen_random_uuid()::text, 1, 13)), 'DISPONIBLE', 'B2-01-02',NOW()),
(9, 'QR-ASIC-ASIC-GRIS-44-' || upper(substr(gen_random_uuid()::text, 1, 13)), 'DISPONIBLE', 'C1-05-08',NOW())
ON CONFLICT (qr_codigo_unico) DO NOTHING;

-- 6. FIX SECUENCIAS
SELECT setval('marcas_id_seq', (SELECT MAX(id) FROM marcas));
SELECT setval('tallas_id_seq', (SELECT MAX(id) FROM tallas));
SELECT setval('productos_id_seq', (SELECT MAX(id) FROM productos));
SELECT setval('variantes_producto_id_seq', (SELECT MAX(id) FROM variantes_producto));
SELECT setval('cajas_stock_id_seq', (SELECT MAX(id) FROM cajas_stock));