-- V12__update_event_spaces_event_support.sql

ALTER TABLE event_spaces
    ADD COLUMN supports_social_events BOOLEAN NOT NULL DEFAULT TRUE,
    ADD COLUMN supports_corporate_events BOOLEAN NOT NULL DEFAULT TRUE;


-- Migrate existing values from event_space_type
UPDATE event_spaces
SET
    supports_social_events =
        CASE
            WHEN LOWER(TRIM(event_space_type)) IN (
                'social',
                'social_event',
                'social_events'
            )
            THEN TRUE
            ELSE FALSE
        END,

    supports_corporate_events =
        CASE
            WHEN LOWER(TRIM(event_space_type)) IN (
                'corporate',
                'corporate_event',
                'corporate_events'
            )
            THEN TRUE
            ELSE FALSE
        END;


ALTER TABLE event_spaces
    DROP COLUMN event_space_type;