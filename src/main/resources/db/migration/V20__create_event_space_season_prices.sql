-- ============================================================
-- V22__create_event_space_season_prices.sql
-- Configure seasonal price adjustments
-- ============================================================

CREATE EXTENSION IF NOT EXISTS btree_gist;

CREATE TABLE event_space_season_prices (
    id UUID PRIMARY KEY,
    event_space_id UUID NOT NULL,
    name VARCHAR(150) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    price_multiplier NUMERIC(6,4) NOT NULL DEFAULT 1.0000,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_event_space_season_prices_event_space
        FOREIGN KEY (event_space_id)
        REFERENCES event_spaces(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_event_space_season_prices_dates
        CHECK (start_date <= end_date),

    CONSTRAINT chk_event_space_season_prices_multiplier
        CHECK (price_multiplier > 0)
);

ALTER TABLE event_space_season_prices
ADD CONSTRAINT ex_event_space_season_prices_no_overlap
EXCLUDE USING gist (
    event_space_id WITH =,
    daterange(start_date, end_date, '[]') WITH &&
)
WHERE (active = TRUE);

CREATE INDEX idx_event_space_season_prices_event_space_id
    ON event_space_season_prices(event_space_id);

CREATE INDEX idx_event_space_season_prices_dates
    ON event_space_season_prices(start_date, end_date);