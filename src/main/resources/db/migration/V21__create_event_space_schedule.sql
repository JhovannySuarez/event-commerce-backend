    -- ============================================================
    -- V22__create_event_space_schedule.sql
    --
    -- Configure event space operating hours and booking extensions.
    --
    -- Default operating hours:
    --   10:00 AM -> 11:59 PM
    --
    -- Each event space can override the schedule per day of week.
    -- Extensions are optional and configured with an absolute
    -- maximum extension time.
    -- ============================================================


    -- ============================================================
    -- Event space operating hours
    -- ============================================================

    CREATE TABLE event_space_operating_hours (
        id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

        event_space_id UUID NOT NULL,

        day_of_week VARCHAR(20) NOT NULL,

        operating_from TIME NOT NULL DEFAULT '10:00:00',

        operating_until TIME NOT NULL DEFAULT '23:59:00',

        created_at TIMESTAMP NOT NULL DEFAULT NOW(),

        updated_at TIMESTAMP NOT NULL DEFAULT NOW(),

        CONSTRAINT fk_event_space_operating_hours_event_space
            FOREIGN KEY (event_space_id)
            REFERENCES event_spaces(id)
            ON DELETE CASCADE,

        CONSTRAINT uk_event_space_operating_hours_day
            UNIQUE (event_space_id, day_of_week),

        CONSTRAINT chk_event_space_operating_hours_day
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
            )
    );


    -- ============================================================
    -- Event space booking configuration
    -- ============================================================

    CREATE TABLE event_space_booking_config (
        id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

        event_space_id UUID NOT NULL,

        extension_allowed BOOLEAN NOT NULL DEFAULT FALSE,

        /*
         * Maximum absolute time until which an event can be extended.
         *
         * Example:
         *   operating_until  = 23:59
         *   extension_until  = 04:00
         *
         * The event may be extended until 04:00 AM.
         */
        extension_until TIME,

        created_at TIMESTAMP NOT NULL DEFAULT NOW(),

        updated_at TIMESTAMP NOT NULL DEFAULT NOW(),

        CONSTRAINT fk_event_space_booking_config_event_space
            FOREIGN KEY (event_space_id)
            REFERENCES event_spaces(id)
            ON DELETE CASCADE,

        CONSTRAINT uk_event_space_booking_config_event_space
            UNIQUE (event_space_id),

        CONSTRAINT chk_extension_configuration
            CHECK (
                (extension_allowed = FALSE AND extension_until IS NULL)
                OR
                (extension_allowed = TRUE AND extension_until IS NOT NULL)
            )
    );


    -- ============================================================
    -- Indexes
    -- ============================================================

    CREATE INDEX idx_event_space_operating_hours_event_space_id
        ON event_space_operating_hours(event_space_id);

    CREATE INDEX idx_event_space_booking_config_event_space_id
        ON event_space_booking_config(event_space_id);


    -- ============================================================
    -- Default operating hours for existing event spaces
    --
    -- Every event space starts with:
    --   Monday-Sunday: 10:00 -> 23:59
    --
    -- Venues can later customize these values per event space/day.
    -- ============================================================

    INSERT INTO event_space_operating_hours (
        event_space_id,
        day_of_week,
        operating_from,
        operating_until
    )
    SELECT
        es.id,
        days.day_of_week,
        '10:00:00',
        '23:59:00'
    FROM event_spaces es
    CROSS JOIN (
        VALUES
            ('MONDAY'),
            ('TUESDAY'),
            ('WEDNESDAY'),
            ('THURSDAY'),
            ('FRIDAY'),
            ('SATURDAY'),
            ('SUNDAY')
    ) AS days(day_of_week);


    -- ============================================================
    -- Default booking configuration for existing event spaces
    --
    -- Extensions are disabled by default.
    -- ============================================================

    INSERT INTO event_space_booking_config (
        event_space_id,
        extension_allowed,
        extension_until
    )
    SELECT
        es.id,
        FALSE,
        NULL
    FROM event_spaces es;