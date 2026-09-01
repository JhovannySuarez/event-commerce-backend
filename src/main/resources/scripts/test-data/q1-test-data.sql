-- ============================================================
-- Q1 TEST DATA
-- ============================================================
--
-- Development / performance test data.
--
-- NOT a Flyway migration.
--
-- Generates:
--   - deterministic scenarios for 2026-08-30
--   - 800 additional events
--   - valid event_space/event_type combinations
--   - hourly and full-day events
--   - active/inactive events
--   - different statuses
--   - different dates
--
-- ============================================================

BEGIN;


-- ============================================================
-- CONSTANTS
-- ============================================================

-- Tenant
-- f2f21f19-0a8c-48bf-acda-517e2ed139f1

-- Venues
-- Medellín
-- 33373db8-94e8-4620-9715-f6e9aa192a9f
--
-- Bogotá
-- f9f0e366-c6ed-4295-8ede-e13eca6befab

-- Event types
--
-- Evento Social
-- 807b4e74-1214-4596-b624-7493d6ca84c7
--
-- Evento Corporativo
-- 1be9427a-13a2-465d-91be-88d712f0c089
--
-- Grandes Eventos
-- 01bde276-ac8c-4688-81fb-f3b76556087a


-- ============================================================
-- CLEAN PREVIOUS Q1 TEST DATA
-- ============================================================

DELETE FROM calendar_event_spaces
WHERE calendar_event_id IN (
    SELECT id
    FROM venue_calendar_events
    WHERE title LIKE 'Q1 TEST %'
);

DELETE FROM venue_calendar_events
WHERE title LIKE 'Q1 TEST %';


-- ============================================================
-- DETERMINISTIC TEST SCENARIOS
--
-- Date: 2026-08-30
--
-- These records are intentionally controlled so we can
-- validate Q1 manually.
-- ============================================================


-- ============================================================
-- SCENARIO 1
--
-- Salón Principal
-- Evento Corporativo
-- HOURLY
-- minimum_hours = 4
--
-- Occupied:
--
--   10:00 - 12:00
--   14:00 - 18:00
--
-- Available:
--
--   12:00 - 14:00  -> 2 hours
--   18:00 - 23:59 -> approximately 6 hours
--
-- Because minimum_hours = 4:
--
--   12:00 - 14:00 MUST NOT be returned
--   18:00 - 23:59 SHOULD be returned
-- ============================================================

INSERT INTO venue_calendar_events (
    id,
    tenant_id,
    venue_id,
    event_type_id,
    title,
    description,
    start_at,
    end_at,
    status,
    source,
    active,
    created_at,
    updated_at,
    event_display_name,
    guest_count
)
VALUES
(
    '20000000-0000-0000-0000-000000000001',
    'f2f21f19-0a8c-48bf-acda-517e2ed139f1',
    '33373db8-94e8-4620-9715-f6e9aa192a9f',
    '1be9427a-13a2-465d-91be-88d712f0c089',
    'Q1 TEST Principal 10-12',
    'Q1 deterministic scenario',
    '2026-08-30 10:00:00',
    '2026-08-30 12:00:00',
    'CONFIRMED',
    'MANUAL',
    TRUE,
    NOW(),
    NOW(),
    'Q1 Test - Corporate',
    80
),
(
    '20000000-0000-0000-0000-000000000002',
    'f2f21f19-0a8c-48bf-acda-517e2ed139f1',
    '33373db8-94e8-4620-9715-f6e9aa192a9f',
    '1be9427a-13a2-465d-91be-88d712f0c089',
    'Q1 TEST Principal 14-18',
    'Q1 deterministic scenario',
    '2026-08-30 14:00:00',
    '2026-08-30 18:00:00',
    'CONFIRMED',
    'MANUAL',
    TRUE,
    NOW(),
    NOW(),
    'Q1 Test - Corporate',
    100
);

INSERT INTO calendar_event_spaces (
    id,
    calendar_event_id,
    event_space_id,
    active,
    created_at
)
VALUES
(
    '21000000-0000-0000-0000-000000000001',
    '20000000-0000-0000-0000-000000000001',
    '10000000-0000-0000-0000-000000000001',
    TRUE,
    NOW()
),
(
    '21000000-0000-0000-0000-000000000002',
    '20000000-0000-0000-0000-000000000002',
    '10000000-0000-0000-0000-000000000001',
    TRUE,
    NOW()
);


-- ============================================================
-- SCENARIO 2
--
-- Auditorio
-- Evento Corporativo
-- HOURLY
-- minimum_hours = 2
--
-- Occupied:
--
--   10:00 - 12:00
--
-- Available:
--
--   12:00 - 23:59
--
-- Should be returned.
-- ============================================================

INSERT INTO venue_calendar_events (
    id,
    tenant_id,
    venue_id,
    event_type_id,
    title,
    description,
    start_at,
    end_at,
    status,
    source,
    active,
    created_at,
    updated_at,
    event_display_name,
    guest_count
)
VALUES
(
    '20000000-0000-0000-0000-000000000003',
    'f2f21f19-0a8c-48bf-acda-517e2ed139f1',
    '33373db8-94e8-4620-9715-f6e9aa192a9f',
    '1be9427a-13a2-465d-91be-88d712f0c089',
    'Q1 TEST Auditorium 10-12',
    'Q1 deterministic scenario',
    '2026-08-30 10:00:00',
    '2026-08-30 12:00:00',
    'CONFIRMED',
    'MANUAL',
    TRUE,
    NOW(),
    NOW(),
    'Q1 Test - Corporate',
    150
);

INSERT INTO calendar_event_spaces (
    id,
    calendar_event_id,
    event_space_id,
    active,
    created_at
)
VALUES
(
    '21000000-0000-0000-0000-000000000003',
    '20000000-0000-0000-0000-000000000003',
    '10000000-0000-0000-0000-000000000002',
    TRUE,
    NOW()
);


-- ============================================================
-- SCENARIO 3
--
-- Salón Social
-- Evento Social
-- FULL_DAY
--
-- Entire operating day occupied.
--
-- Therefore Q1 must NOT return this space.
-- ============================================================

INSERT INTO venue_calendar_events (
    id,
    tenant_id,
    venue_id,
    event_type_id,
    title,
    description,
    start_at,
    end_at,
    status,
    source,
    active,
    created_at,
    updated_at,
    event_display_name,
    guest_count
)
VALUES
(
    '20000000-0000-0000-0000-000000000004',
    'f2f21f19-0a8c-48bf-acda-517e2ed139f1',
    'f9f0e366-c6ed-4295-8ede-e13eca6befab',
    '807b4e74-1214-4596-b624-7493d6ca84c7',
    'Q1 TEST Social FULL DAY',
    'Q1 deterministic FULL_DAY scenario',
    '2026-08-30 10:00:00',
    '2026-08-30 23:59:00',
    'CONFIRMED',
    'MANUAL',
    TRUE,
    NOW(),
    NOW(),
    'Q1 Test - Social',
    90
);

INSERT INTO calendar_event_spaces (
    id,
    calendar_event_id,
    event_space_id,
    active,
    created_at
)
VALUES
(
    '21000000-0000-0000-0000-000000000004',
    '20000000-0000-0000-0000-000000000004',
    '10000000-0000-0000-0000-000000000003',
    TRUE,
    NOW()
);


-- ============================================================
-- SCENARIO 4
--
-- Salón Corporativo
-- Evento Corporativo
-- HOURLY
-- minimum_hours = 4
--
-- Occupied:
--
--   10:00 - 13:00
--   15:00 - 17:00
--   20:00 - 22:00
--
-- Available:
--
--   13:00 - 15:00  -> 2h
--   17:00 - 20:00  -> 3h
--   22:00 - 23:59 -> <4h
--
-- None satisfy minimum_hours = 4.
--
-- Therefore this space MUST NOT be returned.
-- ============================================================

INSERT INTO venue_calendar_events (
    id,
    tenant_id,
    venue_id,
    event_type_id,
    title,
    description,
    start_at,
    end_at,
    status,
    source,
    active,
    created_at,
    updated_at,
    event_display_name,
    guest_count
)
VALUES
(
    '20000000-0000-0000-0000-000000000005',
    'f2f21f19-0a8c-48bf-acda-517e2ed139f1',
    'f9f0e366-c6ed-4295-8ede-e13eca6befab',
    '1be9427a-13a2-465d-91be-88d712f0c089',
    'Q1 TEST Corporate 10-13',
    'Q1 minimum hours scenario',
    '2026-08-30 10:00:00',
    '2026-08-30 13:00:00',
    'CONFIRMED',
    'MANUAL',
    TRUE,
    NOW(),
    NOW(),
    'Q1 Test - Corporate',
    50
),
(
    '20000000-0000-0000-0000-000000000006',
    'f2f21f19-0a8c-48bf-acda-517e2ed139f1',
    'f9f0e366-c6ed-4295-8ede-e13eca6befab',
    '1be9427a-13a2-465d-91be-88d712f0c089',
    'Q1 TEST Corporate 15-17',
    'Q1 minimum hours scenario',
    '2026-08-30 15:00:00',
    '2026-08-30 17:00:00',
    'CONFIRMED',
    'MANUAL',
    TRUE,
    NOW(),
    NOW(),
    'Q1 Test - Corporate',
    40
),
(
    '20000000-0000-0000-0000-000000000007',
    'f2f21f19-0a8c-48bf-acda-517e2ed139f1',
    'f9f0e366-c6ed-4295-8ede-e13eca6befab',
    '1be9427a-13a2-465d-91be-88d712f0c089',
    'Q1 TEST Corporate 20-22',
    'Q1 minimum hours scenario',
    '2026-08-30 20:00:00',
    '2026-08-30 22:00:00',
    'CONFIRMED',
    'MANUAL',
    TRUE,
    NOW(),
    NOW(),
    'Q1 minimum hours scenario',
    30
);

INSERT INTO calendar_event_spaces (
    id,
    calendar_event_id,
    event_space_id,
    active,
    created_at
)
VALUES
(
    '21000000-0000-0000-0000-000000000005',
    '20000000-0000-0000-0000-000000000005',
    '10000000-0000-0000-0000-000000000004',
    TRUE,
    NOW()
),
(
    '21000000-0000-0000-0000-000000000006',
    '20000000-0000-0000-0000-000000000006',
    '10000000-0000-0000-0000-000000000004',
    TRUE,
    NOW()
),
(
    '21000000-0000-0000-0000-000000000007',
    '20000000-0000-0000-0000-000000000007',
    '10000000-0000-0000-0000-000000000004',
    TRUE,
    NOW()
);


-- ============================================================
-- BULK DATA
-- ============================================================
--
-- Generate 800 events.
--
-- IMPORTANT:
-- 2026-08-30 is intentionally excluded from this bulk data
-- so the deterministic scenarios above remain isolated.
--
-- Valid combinations only:
--
--   Space 1 -> Social / Corporate / Grandes Eventos
--   Space 2 -> Corporate / Grandes Eventos
--   Space 3 -> Social
--   Space 4 -> Corporate
--
-- ============================================================

CREATE TEMP TABLE q1_generated_events (
    event_id UUID NOT NULL,
    event_space_id UUID NOT NULL,
    event_type_id UUID NOT NULL,
    start_at TIMESTAMP NOT NULL,
    end_at TIMESTAMP NOT NULL
);


-- ============================================================
-- Generate valid event combinations
-- ============================================================

INSERT INTO q1_generated_events (
    event_id,
    event_space_id,
    event_type_id,
    start_at,
    end_at
)
SELECT
    gen_random_uuid(),

    x.event_space_id,

    x.event_type_id,

    x.start_at,

    CASE
        WHEN x.booking_mode = 'FULL_DAY'
            THEN date_trunc('day', x.start_at) + TIME '23:59:00'
        ELSE
            x.start_at + x.duration
    END

FROM (
    SELECT
        gs,

        CASE
            WHEN gs % 7 IN (0, 1, 2) THEN
                '10000000-0000-0000-0000-000000000001'::uuid

            WHEN gs % 7 IN (3, 4) THEN
                '10000000-0000-0000-0000-000000000002'::uuid

            WHEN gs % 7 = 5 THEN
                '10000000-0000-0000-0000-000000000003'::uuid

            ELSE
                '10000000-0000-0000-0000-000000000004'::uuid
        END AS event_space_id,

        CASE
            -- Salón Principal
            WHEN gs % 7 IN (0, 1) THEN
                '807b4e74-1214-4596-b624-7493d6ca84c7'::uuid

            WHEN gs % 7 = 2 THEN
                '1be9427a-13a2-465d-91be-88d712f0c089'::uuid

            -- Auditorio
            WHEN gs % 7 = 3 THEN
                '1be9427a-13a2-465d-91be-88d712f0c089'::uuid

            WHEN gs % 7 = 4 THEN
                '01bde276-ac8c-4688-81fb-f3b76556087a'::uuid

            -- Salón Social
            WHEN gs % 7 = 5 THEN
                '807b4e74-1214-4596-b624-7493d6ca84c7'::uuid

            -- Salón Corporativo
            ELSE
                '1be9427a-13a2-465d-91be-88d712f0c089'::uuid
        END AS event_type_id,

        CASE
            WHEN gs % 7 IN (0, 1, 4, 5) THEN
                'FULL_DAY'
            ELSE
                'HOURLY'
        END AS booking_mode,

        (
            DATE '2026-08-01'
            + ((gs * 3) % 210)
            + (
                CASE
                    WHEN gs % 6 = 0 THEN TIME '10:00'
                    WHEN gs % 6 = 1 THEN TIME '12:00'
                    WHEN gs % 6 = 2 THEN TIME '14:00'
                    WHEN gs % 6 = 3 THEN TIME '16:00'
                    WHEN gs % 6 = 4 THEN TIME '18:00'
                    ELSE TIME '20:00'
                END
            )
        ) AS start_at,

        CASE
            WHEN gs % 5 = 0 THEN INTERVAL '2 hours'
            WHEN gs % 5 = 1 THEN INTERVAL '3 hours'
            WHEN gs % 5 = 2 THEN INTERVAL '4 hours'
            WHEN gs % 5 = 3 THEN INTERVAL '5 hours'
            ELSE INTERVAL '6 hours'
        END AS duration

    FROM generate_series(1, 800) gs
) x
WHERE x.start_at::date <> DATE '2026-08-30';


-- ============================================================
-- Insert venue_calendar_events
-- ============================================================

INSERT INTO venue_calendar_events (
    id,
    tenant_id,
    venue_id,
    event_type_id,
    title,
    description,
    start_at,
    end_at,
    status,
    source,
    active,
    created_at,
    updated_at,
    event_display_name,
    guest_count
)
SELECT
    e.event_id,

    'f2f21f19-0a8c-48bf-acda-517e2ed139f1'::uuid,

    CASE
        WHEN e.event_space_id IN (
            '10000000-0000-0000-0000-000000000001'::uuid,
            '10000000-0000-0000-0000-000000000002'::uuid
        )
        THEN
            '33373db8-94e8-4620-9715-f6e9aa192a9f'::uuid

        ELSE
            'f9f0e366-c6ed-4295-8ede-e13eca6befab'::uuid
    END,

    e.event_type_id,

    'Q1 TEST Bulk Event',

    'Generated test data for VenueSearchRepository Q1',

    e.start_at,

    e.end_at,

    CASE
        WHEN row_number() OVER (ORDER BY e.event_id) % 10 = 0
            THEN 'TENTATIVE'

        WHEN row_number() OVER (ORDER BY e.event_id) % 15 = 0
            THEN 'CANCELLED'

        ELSE 'CONFIRMED'
    END,

    'MANUAL',

    CASE
        WHEN row_number() OVER (ORDER BY e.event_id) % 17 = 0
            THEN FALSE

        ELSE TRUE
    END,

    NOW(),
    NOW(),

    'Q1 Test Event',

    20 + (
        row_number() OVER (ORDER BY e.event_id) % 250
    )

FROM q1_generated_events e;


-- ============================================================
-- Insert calendar_event_spaces
-- ============================================================

INSERT INTO calendar_event_spaces (
    id,
    calendar_event_id,
    event_space_id,
    active,
    created_at
)
SELECT
    gen_random_uuid(),

    e.event_id,

    e.event_space_id,

    TRUE,

    NOW()

FROM q1_generated_events e;


-- ============================================================
-- Commit
-- ============================================================

COMMIT;


-- ============================================================
-- VERIFICATION
-- ============================================================


-- Total test events
SELECT
    COUNT(*) AS total_q1_test_events
FROM venue_calendar_events
WHERE title LIKE 'Q1 TEST %';


-- Total event-space relationships
SELECT
    COUNT(*) AS total_q1_test_event_spaces
FROM calendar_event_spaces ces
JOIN venue_calendar_events vce
    ON vce.id = ces.calendar_event_id
WHERE vce.title LIKE 'Q1 TEST %';


-- ============================================================
-- Distribution by event space
-- ============================================================

SELECT
    es.name AS event_space,
    COUNT(*) AS total_events
FROM calendar_event_spaces ces
JOIN venue_calendar_events vce
    ON vce.id = ces.calendar_event_id
JOIN event_spaces es
    ON es.id = ces.event_space_id
WHERE vce.title LIKE 'Q1 TEST %'
GROUP BY es.name
ORDER BY es.name;


-- ============================================================
-- Distribution by event space + event type
-- ============================================================

SELECT
    es.name AS event_space,
    et.name AS event_type,
    COUNT(*) AS total_events
FROM calendar_event_spaces ces
JOIN venue_calendar_events vce
    ON vce.id = ces.calendar_event_id
JOIN event_spaces es
    ON es.id = ces.event_space_id
JOIN event_types et
    ON et.id = vce.event_type_id
WHERE vce.title LIKE 'Q1 TEST %'
GROUP BY
    es.name,
    et.name
ORDER BY
    es.name,
    et.name;


-- ============================================================
-- Deterministic scenarios for 2026-08-30
-- ============================================================

SELECT
    es.name AS event_space,
    et.name AS event_type,
    vce.title,
    vce.start_at,
    vce.end_at,
    vce.status,
    vce.active
FROM calendar_event_spaces ces
JOIN venue_calendar_events vce
    ON vce.id = ces.calendar_event_id
JOIN event_spaces es
    ON es.id = ces.event_space_id
JOIN event_types et
    ON et.id = vce.event_type_id
WHERE vce.title LIKE 'Q1 TEST %'
  AND vce.start_at::date = DATE '2026-08-30'
ORDER BY
    es.name,
    vce.start_at;