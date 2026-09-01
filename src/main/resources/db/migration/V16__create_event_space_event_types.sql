-- ============================================================
-- V16__create_event_space_event_types.sql
-- ============================================================

CREATE TABLE event_space_event_types (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    event_space_id UUID NOT NULL,
    event_type_id UUID NOT NULL,

    booking_mode VARCHAR(20) NOT NULL,

    minimum_hours INTEGER,

    active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_event_space_event_types_event_space
        FOREIGN KEY (event_space_id)
        REFERENCES event_spaces(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_event_space_event_types_event_type
        FOREIGN KEY (event_type_id)
        REFERENCES event_types(id)
        ON DELETE RESTRICT,

    CONSTRAINT uk_event_space_event_types_space_type
        UNIQUE (event_space_id, event_type_id),

    CONSTRAINT chk_event_space_event_types_booking_mode
        CHECK (
            booking_mode IN ('FULL_DAY', 'HOURLY')
        ),

    CONSTRAINT chk_event_space_event_types_minimum_hours
        CHECK (
            (
                booking_mode = 'FULL_DAY'
                AND minimum_hours IS NULL
            )
            OR
            (
                booking_mode = 'HOURLY'
                AND minimum_hours IS NOT NULL
                AND minimum_hours > 0
            )
        )
);


-- ============================================================
-- Indexes
-- ============================================================

CREATE INDEX idx_event_space_event_types_event_space
    ON event_space_event_types(event_space_id);

CREATE INDEX idx_event_space_event_types_event_type
    ON event_space_event_types(event_type_id);

CREATE INDEX idx_event_space_event_types_active
    ON event_space_event_types(active);


-- ============================================================
-- Remove old event type configuration
-- ============================================================

ALTER TABLE event_spaces
DROP COLUMN supports_social_events;

ALTER TABLE event_spaces
DROP COLUMN supports_corporate_events;