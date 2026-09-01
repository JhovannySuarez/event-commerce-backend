-- ============================================================
-- V21__create_event_space_day_prices.sql
-- Configure base prices per event space and day of week
-- ============================================================

CREATE TABLE event_space_day_prices (
    id UUID PRIMARY KEY,
    event_space_id UUID NOT NULL,
    day_of_week VARCHAR(10) NOT NULL,
    base_price NUMERIC(12,2) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_event_space_day_prices_event_space
        FOREIGN KEY (event_space_id)
        REFERENCES event_spaces(id)
        ON DELETE CASCADE,

    CONSTRAINT uk_event_space_day_prices_day
        UNIQUE (event_space_id, day_of_week),

    CONSTRAINT chk_event_space_day_prices_day
        CHECK (
            day_of_week IN (
                'MONDAY',
                'TUESDAY',
                'WEDNESDAY',
                'THURSDAY',
                'FRIDAY',
                'SATURDAY',
                'SUNDAY'
            )
        ),

    CONSTRAINT chk_event_space_day_prices_amount
        CHECK (base_price >= 0)
);

CREATE INDEX idx_event_space_day_prices_event_space_id
    ON event_space_day_prices(event_space_id);

ALTER TABLE event_spaces
DROP COLUMN base_price;