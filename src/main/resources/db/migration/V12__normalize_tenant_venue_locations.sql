-- ============================================================
-- V12 - Normalize tenant and venue locations
--
-- Location entities (countries, states, cities) already exist.
--
-- This migration:
--   1. Removes legacy location string columns.
--   2. Keeps city_id as the canonical location reference.
--   3. Adds foreign keys to cities.
--   4. Adds indexes for location-based queries.
-- ============================================================


-- ============================================================
-- TENANTS
-- ============================================================

-- city and country are now represented by city_id.
ALTER TABLE tenants
    DROP COLUMN IF EXISTS city,
    DROP COLUMN IF EXISTS country,
    DROP COLUMN IF EXISTS country_code;


-- city_id is now the canonical tenant location.
ALTER TABLE tenants
    ALTER COLUMN city_id SET NOT NULL;


-- Add FK only if it does not already exist.
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_tenants_city'
    ) THEN
        ALTER TABLE tenants
            ADD CONSTRAINT fk_tenants_city
                FOREIGN KEY (city_id)
                REFERENCES cities(id);
    END IF;
END $$;


CREATE INDEX IF NOT EXISTS idx_tenants_city_id
    ON tenants(city_id);


-- ============================================================
-- VENUES
-- ============================================================

-- city and country are now represented by city_id.
ALTER TABLE venues
    DROP COLUMN IF EXISTS city,
    DROP COLUMN IF EXISTS country;


-- city_id is now the canonical venue location.
ALTER TABLE venues
    ALTER COLUMN city_id SET NOT NULL;


-- Add FK only if it does not already exist.
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_venues_city'
    ) THEN
        ALTER TABLE venues
            ADD CONSTRAINT fk_venues_city
                FOREIGN KEY (city_id)
                REFERENCES cities(id);
    END IF;
END $$;


CREATE INDEX IF NOT EXISTS idx_venues_city_id
    ON venues(city_id);