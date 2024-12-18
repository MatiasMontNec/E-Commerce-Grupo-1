-- storedProcedures.sql

-- Procedimiento ID 17 Reporte de usuarios con más queries
/*
CREATE OR REPLACE PROCEDURE reporte_auditoria()
LANGUAGE plpgsql
AS $$
BEGIN
    SELECT idusuario, operacion, COUNT(*) AS cantidad
    FROM auditoria
    GROUP BY idusuario, operacion
    ORDER BY cantidad DESC;
END;
$$;
*/

-- Procedimiento ID 18
CREATE OR REPLACE PROCEDURE registrar_orden(
    p_idcliente BIGINT,
    p_estado VARCHAR(50),
    p_total DECIMAL(10, 2),
    p_productos JSON,
    p_lugar_entrega GEOMETRY(POINT, 0) -- Parámetro para el lugar de entrega
)
LANGUAGE plpgsql
AS $$
DECLARE
v_idorden BIGINT; -- Variable para almacenar el ID de la nueva orden
    v_producto RECORD; -- Variable para iterar sobre los productos
BEGIN
    -- Inserta la nueva orden
INSERT INTO ordenes (fechaorden, estado, idcliente, total)
VALUES (CURRENT_TIMESTAMP, p_estado, p_idcliente, p_total)
    RETURNING idorden INTO v_idorden;

-- Itera sobre los productos del JSON de entrada
FOR v_producto IN
SELECT * FROM json_to_recordset(p_productos) AS (
            idproducto BIGINT,
            cantidad INT,
            preciounitario DECIMAL(10,2)
        )
    LOOP
-- Inserta el detalle de la orden
INSERT INTO detalleordenes (idorden, idproducto, cantidad, preciounitario)
VALUES (v_idorden, v_producto.idproducto, v_producto.cantidad, v_producto.preciounitario);

-- Actualiza el stock del producto
UPDATE productos
SET stock = stock - v_producto.cantidad
WHERE idproducto = v_producto.idproducto;

-- Verifica que el stock no sea negativo
IF (SELECT stock FROM productos WHERE idproducto = v_producto.idproducto) < 0 THEN
            RAISE EXCEPTION 'Stock insuficiente para el producto con ID %', v_producto.idproducto;
END IF;
END LOOP;
END;
$$;

-- Procedimiento ID 19
CREATE OR REPLACE PROCEDURE apply_category_discount(
    p_category_id BIGINT,
    p_discount_percentage DECIMAL
)
    LANGUAGE plpgsql
AS $$
BEGIN
    -- Actualiza los productos en la categoría seleccionada con el descuento
UPDATE productos p
SET precio = precio * (1 - p_discount_percentage / 100)
WHERE p.idcategoria = p_category_id
  AND p.idproducto NOT IN (
    -- Subconsulta para obtener productos vendidos en los últimos 30 días
    SELECT DISTINCT do_.idproducto
    FROM detalleordenes do_
             INNER JOIN ordenes o ON do_.idorden = o.idorden
    WHERE o.fechaorden >= CURRENT_TIMESTAMP - INTERVAL '30 days'
    );

-- Commit de la transacción
COMMIT;

-- Notificación sobre el descuento aplicado
RAISE NOTICE 'Descuento del % aplicado a productos inactivos en la categoría %',
        p_discount_percentage,
        p_category_id;
END;
$$;

--LAB 2: funcionalidad 17
--true = entrega de id 'p_identrega' esta en zona restringida
--false = entrega de id 'p_identrega' NO esta en zona restringida (es decir, dentro de la zona de la tienda que crea la entrega)
CREATE OR REPLACE FUNCTION is_delivery_outside_store_zone(p_identrega BIGINT)
RETURNS BOOLEAN AS $$
DECLARE
v_delivery_point GEOMETRY;
    v_store_zone GEOMETRY;
    v_is_outside BOOLEAN;
BEGIN
    -- Retrieve the delivery point and associated store zone
SELECT
    e.lugarentrega,
    z.geom
INTO
    v_delivery_point,
    v_store_zone
FROM
    public.entregas e
        JOIN
    public.ordenes o ON e.idorden = o.idorden
        JOIN
    public.productos p ON o.idorden = (
        SELECT idorden
        FROM public.detalleordenes
        WHERE idproducto = p.idproducto
    LIMIT 1
    )
    JOIN
    public.tiendas t ON p.idtienda = t.idtienda
    JOIN
    public.zonas z ON z.idtienda = t.idtienda
WHERE
    e.identrega = p_identrega;

-- Check if the delivery point is outside the store zone
-- Use ST_Disjoint to check if the points do not overlap
v_is_outside := ST_Disjoint(v_delivery_point, v_store_zone);

RETURN v_is_outside;
END;
$$ LANGUAGE plpgsql;

-- Example usage:
--SELECT is_delivery_outside_store_zone(1) AS is_outside_zone;
