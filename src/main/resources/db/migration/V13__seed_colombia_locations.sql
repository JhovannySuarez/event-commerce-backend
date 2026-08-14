-- ============================================================
-- V13 - Seed Colombia locations
--
-- Country:
--   Colombia
--
-- States:
--   32 departments + Bogotá D.C.
--
-- Cities:
--   Department capitals
--
-- This migration is idempotent.
-- ============================================================

BEGIN;


-- ============================================================
-- COUNTRY
-- ============================================================

INSERT INTO countries (
    id,
    code,
    name,
    created_at,
    updated_at
)
SELECT
    gen_random_uuid(),
    'CO',
    'Colombia',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
WHERE NOT EXISTS (
    SELECT 1
    FROM countries
    WHERE code = 'CO'
);


-- ============================================================
-- STATES / DEPARTMENTS
-- ============================================================

INSERT INTO states (
    id,
    country_id,
    code,
    name,
    created_at,
    updated_at
)
SELECT
    gen_random_uuid(),
    c.id,
    location.code,
    location.name,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM countries c
CROSS JOIN (
    VALUES
        ('05', 'Antioquia'),
        ('08', 'Atlántico'),
        ('11', 'Bogotá D.C.'),
        ('13', 'Bolívar'),
        ('15', 'Boyacá'),
        ('17', 'Caldas'),
        ('18', 'Caquetá'),
        ('19', 'Cauca'),
        ('20', 'Cesar'),
        ('23', 'Córdoba'),
        ('25', 'Cundinamarca'),
        ('27', 'Chocó'),
        ('41', 'Huila'),
        ('44', 'La Guajira'),
        ('47', 'Magdalena'),
        ('50', 'Meta'),
        ('52', 'Nariño'),
        ('54', 'Norte de Santander'),
        ('63', 'Quindío'),
        ('66', 'Risaralda'),
        ('68', 'Santander'),
        ('70', 'Sucre'),
        ('73', 'Tolima'),
        ('76', 'Valle del Cauca'),
        ('81', 'Arauca'),
        ('85', 'Casanare'),
        ('86', 'Putumayo'),
        ('88', 'San Andrés y Providencia'),
        ('91', 'Amazonas'),
        ('94', 'Guainía'),
        ('95', 'Guaviare'),
        ('97', 'Vaupés'),
        ('99', 'Vichada')
) AS location(code, name)
WHERE c.code = 'CO'
  AND NOT EXISTS (
      SELECT 1
      FROM states s
      WHERE s.country_id = c.id
        AND s.code = location.code
  );


-- ============================================================
-- CITIES
-- ============================================================

INSERT INTO cities (
    id,
    state_id,
    name,
    created_at,
    updated_at
)
SELECT
    gen_random_uuid(),
    s.id,
    location.city_name,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM states s
JOIN countries c
    ON c.id = s.country_id
CROSS JOIN (
    VALUES
        ('05', 'Medellín'),
        ('08', 'Barranquilla'),
        ('11', 'Bogotá'),
        ('13', 'Cartagena de Indias'),
        ('15', 'Tunja'),
        ('17', 'Manizales'),
        ('18', 'Florencia'),
        ('19', 'Popayán'),
        ('20', 'Valledupar'),
        ('23', 'Montería'),
        ('25', 'Bogotá'),
        ('27', 'Quibdó'),
        ('41', 'Neiva'),
        ('44', 'Riohacha'),
        ('47', 'Santa Marta'),
        ('50', 'Villavicencio'),
        ('52', 'Pasto'),
        ('54', 'San José de Cúcuta'),
        ('63', 'Armenia'),
        ('66', 'Pereira'),
        ('68', 'Bucaramanga'),
        ('70', 'Sincelejo'),
        ('73', 'Ibagué'),
        ('76', 'Cali'),
        ('81', 'Arauca'),
        ('85', 'Yopal'),
        ('86', 'Mocoa'),
        ('88', 'San Andrés'),
        ('91', 'Leticia'),
        ('94', 'Inírida'),
        ('95', 'San José del Guaviare'),
        ('97', 'Mitú'),
        ('99', 'Puerto Carreño')
) AS location(state_code, city_name)
WHERE c.code = 'CO'
  AND s.code = location.state_code
  AND NOT EXISTS (
      SELECT 1
      FROM cities existing_city
      WHERE existing_city.state_id = s.id
        AND existing_city.name = location.city_name
  );


COMMIT;