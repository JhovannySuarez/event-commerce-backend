-- ============================================================
-- Location model
-- ============================================================

-- ------------------------------------------------------------
-- Countries
-- ------------------------------------------------------------

CREATE TABLE countries (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(2) NOT NULL,
    name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uk_countries_code
        UNIQUE (code),

    CONSTRAINT uk_countries_name
        UNIQUE (name)
);


-- ------------------------------------------------------------
-- States / Administrative regions
-- ------------------------------------------------------------

CREATE TABLE states (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    country_id UUID NOT NULL,
    code VARCHAR(20),
    name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_states_country
        FOREIGN KEY (country_id)
        REFERENCES countries (id),

    CONSTRAINT uk_states_country_code
        UNIQUE (country_id, code),

    CONSTRAINT uk_states_country_name
        UNIQUE (country_id, name)
);


-- ------------------------------------------------------------
-- Cities
-- ------------------------------------------------------------

CREATE TABLE cities (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    state_id UUID NOT NULL,
    name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_cities_state
        FOREIGN KEY (state_id)
        REFERENCES states (id),

    CONSTRAINT uk_cities_state_name
        UNIQUE (state_id, name)
);


-- ------------------------------------------------------------
-- Indexes
-- ------------------------------------------------------------

CREATE INDEX idx_states_country_id
    ON states (country_id);

CREATE INDEX idx_cities_state_id
    ON cities (state_id);

CREATE INDEX idx_cities_name
    ON cities (name);


-- ------------------------------------------------------------
-- Initial country
-- ------------------------------------------------------------

INSERT INTO countries (
    code,
    name
)
VALUES (
    'CO',
    'Colombia'
);


-- ------------------------------------------------------------
-- Initial state
-- ------------------------------------------------------------

INSERT INTO states (
    country_id,
    code,
    name
)
SELECT
    id,
    'ANT',
    'Antioquia'
FROM countries
WHERE code = 'CO';


-- ------------------------------------------------------------
-- Initial cities
-- ------------------------------------------------------------

INSERT INTO cities (
    state_id,
    name
)
SELECT
    s.id,
    city.name
FROM states s
CROSS JOIN (
    VALUES
        ('Medellín'),
        ('Envigado'),
        ('Itagüí'),
        ('Bello'),
        ('Sabaneta'),
        ('Rionegro'),
        ('La Ceja')
) AS city(name)
WHERE s.code = 'ANT'
  AND s.country_id = (
      SELECT id
      FROM countries
      WHERE code = 'CO'
  );


-- ------------------------------------------------------------
-- Venues
-- ------------------------------------------------------------

ALTER TABLE venues
    ADD COLUMN city_id UUID;

ALTER TABLE venues
    ADD CONSTRAINT fk_venues_city
        FOREIGN KEY (city_id)
        REFERENCES cities (id);

CREATE INDEX idx_venues_city_id
    ON venues (city_id);


-- ------------------------------------------------------------
-- Tenants
-- ------------------------------------------------------------

ALTER TABLE tenants
    ADD COLUMN city_id UUID;

ALTER TABLE tenants
    ADD CONSTRAINT fk_tenants_city
        FOREIGN KEY (city_id)
        REFERENCES cities (id);

CREATE INDEX idx_tenants_city_id
    ON tenants (city_id);


-- ------------------------------------------------------------
-- Migrate existing venue locations
-- ------------------------------------------------------------

UPDATE venues v
SET city_id = c.id
FROM cities c
JOIN states s
    ON s.id = c.state_id
JOIN countries co
    ON co.id = s.country_id
WHERE LOWER(TRIM(v.city)) = LOWER(TRIM(c.name))
  AND LOWER(TRIM(v.country)) = LOWER(TRIM(co.name))
  AND v.city_id IS NULL;


-- ------------------------------------------------------------
-- Migrate existing tenant locations
-- ------------------------------------------------------------

UPDATE tenants t
SET city_id = c.id
FROM cities c
JOIN states s
    ON s.id = c.state_id
JOIN countries co
    ON co.id = s.country_id
WHERE LOWER(TRIM(t.city)) = LOWER(TRIM(c.name))
  AND LOWER(TRIM(t.country)) = LOWER(TRIM(co.name))
  AND t.city_id IS NULL;