-- ============================================================
-- V20__create_event_space_availability.sql
-- Configure operating hours per event space and day of week
-- ============================================================

CREATE TABLE event_space_availability (
    id UUID PRIMARY KEY,
    event_space_id UUID NOT NULL,
    day_of_week VARCHAR(10) NOT NULL,
    available_from TIME NOT NULL,
    available_until TIME NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_event_space_availability_event_space
        FOREIGN KEY (event_space_id)
        REFERENCES event_spaces(id)
        ON DELETE CASCADE,

    CONSTRAINT uk_event_space_availability_day
        UNIQUE (event_space_id, day_of_week),

    CONSTRAINT chk_event_space_availability_day
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

    CONSTRAINT chk_event_space_availability_time
        CHECK (available_from < available_until)
);

CREATE INDEX idx_event_space_availability_event_space_id
    ON event_space_availability(event_space_id);